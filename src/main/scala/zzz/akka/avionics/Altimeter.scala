package zzz.akka.avionics

import akka.actor.{Actor, ActorLogging}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object Altimeter {

  case class RateChange(amount: Float)

  case class AltitudeUpdate(altitude: Double)

  private val ceiling = 43000
  private val maxRateOfClimb = 5000

  private case object Tick

}

class Altimeter
  extends Actor with ActorLogging with EventSource {

  import Altimeter._

  implicit val ec: ExecutionContext = context.dispatcher

  private var rateOfClimb = 0f
  private var altitude = 0d
  private var lastTick = System.currentTimeMillis

  private val ticker = context.system.scheduler.schedule(100.millis, 100.millis, self, Tick)

  private def altimeterReceive: Receive = {
    case RateChange(amount) =>
      rateOfClimb = amount.min(1.0f).max(-1.0f) * maxRateOfClimb
      log.info(s"Altimeter changed rate of climb to $rateOfClimb")

    case Tick =>
      val tick = System.currentTimeMillis
      altitude = altitude + ((tick - lastTick) / 60000.0) * rateOfClimb
      lastTick = tick
      sendEvent(AltitudeUpdate(altitude))
  }

  def receive = eventSourceReceive orElse altimeterReceive

  override def postStop(): Unit = {
    ticker.cancel()
  }

}