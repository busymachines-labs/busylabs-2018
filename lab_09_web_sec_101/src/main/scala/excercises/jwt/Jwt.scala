package excercises.jwt

case class JwtPayload(
  sub:  String,
  name: String,
  role: String,
  iat:  Int
)

case class JwtHeader(
  typ: String,
  alg: String
)
