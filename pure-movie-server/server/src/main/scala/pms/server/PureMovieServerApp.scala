package pms.server

import doobie.util.transactor.Transactor
import pms.effects._
import pms.email._
import pms.db.config._
import fs2.{Stream, StreamApp}
import org.http4s._
import org.http4s.server.blaze._
import pms.algebra.imdb._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 20 Jun 2018
  *
  */
object PureMovieServerApp extends StreamApp[IO] {

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, StreamApp.ExitCode] = {
    implicit val sch: Scheduler = Scheduler.global
    for {
      serverConfig <- Stream.eval(PureMovieServerConfig.default[IO])
      gmailConfig  <- Stream.eval(GmailConfig.default[IO])
      imdbConfig   <- Stream.eval(IMDBConfig.default[IO])
      dbConfig     <- Stream.eval(DatabaseConfig.default[IO])
      transactor   <- Stream.eval(DatabaseAlgebra.transactor[IO](dbConfig))
      _            <- Stream.eval(DatabaseAlgebra.initializeSQLDb[IO](dbConfig))
      pmsModule    <- Stream.eval(pureMovieServerModule[IO](gmailConfig,imdbConfig, transactor))
      exitCode <- serverStream[IO](
                   config  = serverConfig,
                   service = pmsModule.pureMovieServerService
                 )
    } yield exitCode
  }

  private def pureMovieServerModule[F[_]: Concurrent](gmailConfig: GmailConfig, imdbConfig:IMDBConfig, transactor: Transactor[F]): F[ModulePureMovieServer[F]] =
    Concurrent.apply[F].delay(ModulePureMovieServer.concurrent(gmailConfig, imdbConfig)(implicitly, transactor))


  private def serverStream[F[_]: Effect: Concurrent](
    config:      PureMovieServerConfig,
    service:     HttpService[F]
  )(implicit ec: ExecutionContext): Stream[F, StreamApp.ExitCode] =
    BlazeBuilder[F]
      .bindHttp(config.port, config.host)
      .mountService(service, config.apiRoot)
      .serve
}
