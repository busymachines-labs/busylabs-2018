
object  Main extends App{

 def factorial(x: Int): Int = {
   def factorialAux(x: Int, acc: Int): Int =
     if(x == 1) acc else factorialAux(x - 1, x * acc)
   factorialAux(x, 1)
 }

  println(factorial(5))

}


