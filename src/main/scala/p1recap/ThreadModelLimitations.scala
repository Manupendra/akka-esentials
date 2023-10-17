package p1recap

object ThreadModelLimitations extends App {

  // DR #1: OO encapsulation is only valid in the SINGLE-THREADED MODEL
  class BankAccount(private var amount: Int){
    override def toString: String = "" + amount

    def withdraw(money: Int) = this.amount -= money
    def deposit(money: Int) = this.amount += money
    def getAmount = amount
  }

  val account = new BankAccount(2000)
  for(i <- 1 to 1000){
    new Thread(() => account.withdraw(1)).start()
  }
  for(i <- 1 to 1000){
    new Thread(() => account.deposit(1)).start()
  }
  println(account.getAmount)

}
