package zzz.akka.avionics.main

import akka.actor.Props
import zzz.akka.avionics.{AttendantCreationPolicy, LeadFlightAttendant}

object FlightAttendantPathChecker {

  def main(args: Array[String]): Unit ={
    val system = akka.actor.ActorSystem("PlaneSimulation")
    val lead = system.actorOf(
      Props(new LeadFlightAttendant with AttendantCreationPolicy),
      system.settings.config.getString("zzz.akka.avionics.flightcrew.leadAttendantName")
    )

    Thread.sleep(2000)
    system.shutdown()
  }

}
