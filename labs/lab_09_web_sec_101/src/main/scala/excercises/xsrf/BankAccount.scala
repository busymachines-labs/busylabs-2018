package excercises.xsrf

case class BankAccount(name: String, amount: Int) {
  def transfer(to: String, amount: Int): BankAccount = {
    BankAccount(name, this.amount - amount)
  }
}
