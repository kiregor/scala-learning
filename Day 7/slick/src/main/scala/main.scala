import slick.dbio.Effect
import slick.jdbc.MySQLProfile.api._
import slick.sql.FixedSqlAction

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

object main extends App{
  val db = Database.forConfig(path = "mysqlDB")
  val peopleTable = TableQuery[People]
  val dropPeopleCmd = DBIO.seq(peopleTable.schema.drop)
  val initPeopleCmd = DBIO.seq(peopleTable.schema.create)

  def dropDB: Future[Unit] = {
    val dropFuture = Future{db.run(dropPeopleCmd)}

    Await.result(dropFuture, Duration.Inf).andThen{
      case Success(_) => initialisePeople
      case Failure(error) =>
        println("Dropping the table failed due to: " + error.getMessage)
        initialisePeople
    }
  }

  def initialisePeople: Future[Unit] = {
    val setupFuture = Future {
      db.run(initPeopleCmd)
    }

    Await.result(setupFuture, Duration.Inf).andThen {
      case Success(_) => runQuery
      case Failure(error) =>
        println("Intialising the table failed due to: " + error.getMessage)
    }
  }

  def runQuery: Future[Option[Int]] = {
    val insertPeople = Future {
      val query = peopleTable ++= Seq (
        (10, "Jack", "Wood", 36),
        (20, "Tim", "Brown", 24)
      )

      println(query.statements.head)
      db.run(query)
    }

    Await.result(insertPeople, Duration.Inf).andThen {
      case Success(_) => searchPeople
      case Failure(error) => println("Query failed to run due to: " + error.getMessage)
    }
  }

  def listPeople: Future[Unit] = {
    val queryFuture = Future {
      db.run(peopleTable.result).map(_.foreach {
        case (id, fName, lName, age) => println(s" $id $fName $lName $age")
      })
    }

    Await.result(queryFuture, Duration.Inf).andThen {
      case Success(_) => db.close()
      case Failure(error) =>
        println("Listing people failed due to: " + error.getMessage)
    }
  }

  def updatePeople: Future[Int] = {

    val queryFuture = Future {
      db.run(peopleTable.filter(_.id === 1).map(p => p.fName).update("John"))
    }

    Await.result(queryFuture, Duration.Inf).andThen {
      case Success(_) => deletePeople
      case Failure(error) =>
        println("Updating people failed due to: " + error.getMessage)
    }
  }

  def searchPeople: Future[Unit] = {

    val queryFuture = Future {
      db.run(peopleTable.filter(p => p.id === 1).result).map(_.foreach {
        case (id, fName, lName, age) => println(s" $id $fName $lName $age")
      })
    }

    Await.result(queryFuture, Duration.Inf).andThen {
      case Success(_) => updatePeople
      case Failure(error) =>
        println("Searching people failed due to: " + error.getMessage)
    }
  }

  def deletePeople:Future[Int] = {

    val queryFuture = Future {
      db.run(peopleTable.filter(p => p.fName === "John").delete)
    }

    Await.result(queryFuture, Duration.Inf).andThen{
      case Success(_) => listPeople
      case Failure(error) =>
        println("Deleting people failed due to: " + error.getMessage)
    }
  }

  dropDB
  Thread.sleep(10000)
}
