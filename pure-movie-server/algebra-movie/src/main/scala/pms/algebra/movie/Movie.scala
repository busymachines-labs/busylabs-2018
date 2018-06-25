package pms.algebra.movie

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
final case class Movie(
  id:   MovieID,
  name: MovieName,
  date: Option[ReleaseDate],
)
