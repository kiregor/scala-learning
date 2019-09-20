import slick.jdbc.MySQLProfile.api._
import slick.lifted.ProvenShape

class People (tag: Tag) extends Table[(Int, String, String, Int)](tag, "PEOPLE"){
  def id: Rep[Int] = column[Int]("PER_ID", O.PrimaryKey, O.AutoInc)
  def fName: Rep[String] = column[String]("PER_FNAME")
  def lName: Rep[String] = column[String]("PER_LNAME")
  def age: Rep[Int] = column[Int]("PER_AGE")
  override def * : ProvenShape[(Int, String, String, Int)] = (id, fName, lName, age)
}
