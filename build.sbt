import sbt._
import sbtassembly.MergeStrategy
import sbtassembly.PathList

lazy val root =
  Project(id = "busylabs", base = file("."))
    .settings(commonsSettings)
    .aggregate(
      email,
      jwt,
      lab_02,
      lab_03,
      lab_04,
      lab_05,
      lab_06,
      lab_07,
      lab_07_second,
      lab_08,
      lab_09_web_sec_101
    )

lazy val email = project
  .settings(commonsSettings)
  .settings(sbtAssemblySettings)

lazy val jwt = project
  .settings(commonsSettings)
  .settings(sbtAssemblySettings)

//equivalent to: Project(id = "lab_02", base = file("./lab_02"))
lazy val lab_02 = project
  .settings(commonsSettings)
  .settings(sbtAssemblySettings)

lazy val lab_03 = project
  .settings(commonsSettings)
  .settings(sbtAssemblySettings)

lazy val lab_04 = project
  .settings(commonsSettings)
  .settings(sbtAssemblySettings)

lazy val lab_05 = project
  .settings(commonsSettings)
  .settings(sbtAssemblySettings)

lazy val lab_06 = project
  .settings(commonsSettings)
  .settings(sbtAssemblySettings)

lazy val lab_07 = project
  .settings(commonsSettings)
  .settings(sbtAssemblySettings)

lazy val lab_07_second = project
  .settings(commonsSettings)
  .settings(sbtAssemblySettings)

lazy val lab_08 = project
  .settings(commonsSettings)
  .settings(sbtAssemblySettings)

lazy val lab_09_web_sec_101 = project
  .settings(commonsSettings)
  .settings(sbtAssemblySettings)

def commonsSettings: Seq[Setting[_]] = Seq(
  scalaVersion := "2.12.6",
  libraryDependencies ++= Seq(
    //utils
    bmcCore,
    bmcDuration,
    //effects + streams
    catsCore,
    catsEffect,
    monix,
    fs2,
    bmcEffects,
    //JSON stuff
    circeCore,
    circeGeneric,
    circeGenericExtras,
    bmcJson,
    //akka + akka-http
    akkaActor,
    akkaStream,
    akkaHttp,
    akkaHttpCirceIntegration,
    bmcRestCore,
    bmcRestJson,
    //http4s
    http4sBlazeServer,
    http4sCirce,
    //doobie
    doobieHikari,
    doobiePostgres,
    //logging
    log4cats,
    scalaLogging,
    logbackClassic,
    //email
    javaxMail,
    //test stuff
    scalaTest,
    scalaCheck,
    akkaTK,
    akkaHttpTK,
    doobieTK,
    //misc
    attoParser,
    // akka spray json
    akkaSprayJson,
    // database
    mongoCasbah,
    postgresql,
    slick,
    hikari,
    slickAlpakka,
    typeSafeConfig,
    jwtPaulDijou
  ) ++ tsec,
  /*
   * Eliminates useless, unintuitive, and sometimes broken additions of `withFilter`
   * when using generator arrows in for comprehensions. e.g.
   *
   * Vanila scala:
   * {{{
   *   for {
   *      x: Int <- readIntIO
   *      //
   *   } yield ()
   *   // instead of being `readIntIO.flatMap(x: Int => ...)`, it's something like .withFilter {case x: Int}, which is tantamount to
   *   // a runtime instanceof check. Absolutely horrible, and ridiculous, and unintuitive, and contrary to the often-
   *   // parroted mantra of "a for is just sugar for flatMap and map
   * }}}
   *
   * https://github.com/oleg-py/better-monadic-for
   */
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.0"),
  scalacOptions ++= customScalaCompileFlags,
  /**
    * This is here to eliminate eviction warnings from SBT.
    * The eco-system is mid-upgrade, so not all dependencies
    * depend on this newest cats, and cats-effect. But the
    * old versions cats-core 1.0.1, and cats-effect 0.10
    * are guaranteed to be binary compatible with the newer
    * ones which "choose" over them.
    *
    * By guarantee I mean that the library authors ran
    * a binary compatability analysis.
    *
    * See more on binary compatability:
    * https://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html
    *
    * It is an important issue that you need to keep track of if
    * you build apps on the JVM
    */
  dependencyOverrides += "org.typelevel" %% "cats-core"   % "1.1.0",
  dependencyOverrides += "org.typelevel" %% "cats-effect" % "0.10.1"
)

def sbtAssemblySettings: Seq[Setting[_]] = {
  baseAssemblySettings ++
    Seq(
      // Skip tests during while running the assembly task
      test in assembly := {},
      assemblyMergeStrategy in assembly := {
        case PathList("application.conf", _ @_*) => MergeStrategy.concat
        case "application.conf" => MergeStrategy.concat
        case x                  => (assemblyMergeStrategy in assembly).value(x)
      },
      //this is to avoid propagation of the assembly task to all subprojects.
      //changing this makes assembly incredibly slow
      aggregate in assembly := false
    )
}

/**
  * tpolecat's glorious compile flag list:
  * https://tpolecat.github.io/2017/04/25/scalac-flags.html
  */
def customScalaCompileFlags: Seq[String] = Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-encoding",
  "utf-8", // Specify character encoding used by source files.
  "-explaintypes", // Explain type errors in more detail.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-language:higherKinds", // Allow higher-kinded types
  "-language:implicitConversions", // Allow definition of implicit functions called views
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfuture", // Turn on future language features.
  "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
  "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
  "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
  "-Xlint:option-implicit", // Option.apply used implicit view.
  "-Xlint:package-object-classes", // Class or object defined in package object.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
  "-Xlint:unsound-match", // Pattern match may not be typesafe.
  "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals", // Warn if a local definition is unused.
  "-Ywarn-unused:params", // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates", // Warn if a private member is unused.
  "-Ywarn-value-discard",  // Warn when non-Unit expression results are unused.
  "-Ypartial-unification", // Enable partial unification in type constructor inference

  //"-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
  /*
   * These are flags specific to the "better-monadic-for" plugin:
   * https://github.com/oleg-py/better-monadic-for
   */
  "-P:bm4:no-filtering:y", // see https://github.com/oleg-py/better-monadic-for#desugaring-for-patterns-without-withfilters--pbm4no-filteringy
  "-P:bm4:no-map-id:y", // see https://github.com/oleg-py/better-monadic-for#final-map-optimization--pbm4no-map-idy
  "-P:bm4:no-tupling:y" // see https://github.com/oleg-py/better-monadic-for#desugar-bindings-as-vals-instead-of-tuples--pbm4no-tuplingy
)

//https://github.com/busymachines/busymachines-commons
def bmCommons(m: String): ModuleID = "com.busymachines" %% s"busymachines-commons-$m" % "0.3.0-RC8"

lazy val bmcCore:          ModuleID = bmCommons("core")              withSources ()
lazy val bmcDuration:      ModuleID = bmCommons("duration")          withSources ()
lazy val bmcEffects:       ModuleID = bmCommons("effects")           withSources ()
lazy val bmcEffectsSync:   ModuleID = bmCommons("effects-sync")      withSources ()
lazy val bmcEffectsSyncC:  ModuleID = bmCommons("effects-sync-cats") withSources ()
lazy val bmcEffectsAsync:  ModuleID = bmCommons("effects-async")     withSources ()
lazy val bmcJson:          ModuleID = bmCommons("json")              withSources ()
lazy val bmcRestCore:      ModuleID = bmCommons("rest-core")         withSources ()
lazy val bmcRestJson:      ModuleID = bmCommons("rest-json")         withSources ()
lazy val bmcSemVer:        ModuleID = bmCommons("semver")            withSources ()
lazy val bmcSemVerParsers: ModuleID = bmCommons("semver-parsers")    withSources ()
lazy val bmcRestJsonTK:    ModuleID = bmCommons("rest-json-testkit") % Test withSources ()
lazy val bmcRestCoreTK:    ModuleID = bmCommons("rest-core-testkit") % Test withSources ()

//============================================================================================
//================================= http://typelevel.org/scala/ ==============================
//========================================  typelevel ========================================
//============================================================================================

//https://typelevel.org/cats/
lazy val catsCore: ModuleID = "org.typelevel" %% "cats-core" % "1.1.0" withSources ()
//https://typelevel.org/cats-effect/
lazy val catsEffect: ModuleID = "org.typelevel" %% "cats-effect" % "0.10.1" withSources ()

//https://monix.io/
lazy val monix: ModuleID = "io.monix" %% "monix" % "3.0.0-RC1" withSources ()

//https://functional-streams-for-scala.github.io/fs2/
lazy val fs2: ModuleID = "co.fs2" %% "fs2-core" % "0.10.3" withSources ()

//https://circe.github.io/circe/
lazy val circeVersion: String = "0.9.3"

lazy val circeCore:          ModuleID = "io.circe" %% "circe-core"           % circeVersion
lazy val circeGeneric:       ModuleID = "io.circe" %% "circe-generic"        % circeVersion
lazy val circeGenericExtras: ModuleID = "io.circe" %% "circe-generic-extras" % circeVersion

lazy val attoParser: ModuleID = "org.tpolecat" %% "atto-core" % "0.6.2" withSources ()

//https://http4s.org/
lazy val Http4sVersion = "0.18.9"

lazy val http4sBlazeServer = "org.http4s" %% "http4s-blaze-server" % Http4sVersion withSources ()
lazy val http4sCirce       = "org.http4s" %% "http4s-circe"        % Http4sVersion withSources ()
lazy val http4sDSL         = "org.http4s" %% "http4s-dsl"          % Http4sVersion withSources ()

// http://tpolecat.github.io/doobie/
lazy val doobieVersion = "0.5.2"

lazy val doobieHikari   = "org.tpolecat" %% "doobie-hikari"    % "0.5.2" withSources () // HikariCP transactor.
lazy val doobiePostgres = "org.tpolecat" %% "doobie-postgres"  % "0.5.2" withSources () // Postgres driver 42.2.2 + type mappings.
lazy val doobieTK       = "org.tpolecat" %% "doobie-scalatest" % "0.5.2" % Test withSources () // ScalaTest support for typechecking statements.

val tsecV = "0.0.1-M11"

lazy val tsec = Seq(
  "io.github.jmcardon" %% "tsec-common"        % tsecV,
  "io.github.jmcardon" %% "tsec-password"      % tsecV,
  "io.github.jmcardon" %% "tsec-cipher-jca"    % tsecV,
  "io.github.jmcardon" %% "tsec-cipher-bouncy" % tsecV,
  "io.github.jmcardon" %% "tsec-mac"           % tsecV,
  "io.github.jmcardon" %% "tsec-signatures"    % tsecV,
  "io.github.jmcardon" %% "tsec-hash-jca"      % tsecV,
  "io.github.jmcardon" %% "tsec-hash-bouncy"   % tsecV,
  "io.github.jmcardon" %% "tsec-libsodium"     % tsecV,
  "io.github.jmcardon" %% "tsec-jwt-mac"       % tsecV,
  "io.github.jmcardon" %% "tsec-jwt-sig"       % tsecV,
  "io.github.jmcardon" %% "tsec-http4s"        % tsecV
)

//============================================================================================
//================================= http://akka.io/docs/ =====================================
//======================================== akka ==============================================
//============================================================================================

lazy val akkaVersion: String = "2.5.11"

lazy val akkaActor:  ModuleID = "com.typesafe.akka" %% "akka-actor"  % akkaVersion
lazy val akkaStream: ModuleID = "com.typesafe.akka" %% "akka-stream" % akkaVersion

lazy val akkaHttpVersion: String   = "10.1.1"
lazy val akkaHttp:        ModuleID = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion

lazy val akkaSprayJson: ModuleID = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion

/**
  * https://github.com/hseeberger/akka-http-json
  */
lazy val akkaHttpCirceIntegration: ModuleID = "de.heikoseeberger" %% "akka-http-circe" % "1.20.1"

lazy val akkaTK:     ModuleID = "com.typesafe.akka" %% "akka-testkit"      % akkaVersion     % Test withSources ()
lazy val akkaHttpTK: ModuleID = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test withSources ()

//============================================================================================
//=========================================  logging =========================================
//============================================================================================
lazy val log4cats = "io.chrisdavenport" %% "log4cats-slf4j" % "0.0.5"

lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2" withSources ()
//this is a Java library, notice that we used one single % instead of %%
lazy val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.2.3" withSources ()

//============================================================================================
//==========================================  email ==========================================
//============================================================================================

//this is a Java library, notice that we used one single % instead of %%
lazy val javaxMail = "com.sun.mail" % "javax.mail" % "1.6.1" withSources ()

//============================================================================================
//=========================================  testing =========================================
//============================================================================================

lazy val scalaTest:  ModuleID = "org.scalatest"  %% "scalatest"  % "3.0.5"  % Test withSources ()
lazy val scalaCheck: ModuleID = "org.scalacheck" %% "scalacheck" % "1.13.5" % Test withSources ()

//============================================================================================
//========================================= database =========================================
//============================================================================================

lazy val mongoCasbah    = "org.mongodb"        %% "casbah"                    % "3.1.1" pomOnly ()
lazy val postgresql     = "org.postgresql"     % "postgresql"                 % "9.3-1100-jdbc4"
lazy val slick          = "com.typesafe.slick" %% "slick"                     % "2.1.0"
lazy val hikari         = "com.zaxxer"         % "HikariCP"                   % "3.1.0"
lazy val slickAlpakka   = "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "0.18"
lazy val typeSafeConfig = "com.typesafe"       % "config"                     % "1.3.2"

//
lazy val jwtPaulDijou = "com.pauldijou" %% "jwt-core" % "0.16.0"
