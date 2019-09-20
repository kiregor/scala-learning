name := "slick"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies += "com.typesafe.slick" %% "slick" % "3.3.2"
libraryDependencies +=  "org.slf4j" % "slf4j-nop" % "1.6.4"
libraryDependencies += "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2"
libraryDependencies += "mysql" % "mysql-connector-java" % "6.0.6"

resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"