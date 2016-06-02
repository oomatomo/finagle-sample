package com.oomatomo.finaglesample

import com.twitter.finagle._
import com.twitter.finagle.http.{ Request, Response }
import com.twitter.finagle.http.service.RoutingService
import com.twitter.finagle.http.path._
import com.twitter.util.{ Await, Future }

import com.twitter.finagle.redis.Client
import com.twitter.finagle.redis.util.{RedisCluster, StringToChannelBuffer}

// Simple filter
class SampleFilter[Request, Response]()
    extends SimpleFilter[http.Request, http.Response] {

    override def apply(request: http.Request, service: Service[http.Request, http.Response]): Future[http.Response] = {
        if (request.params.get("test").isDefined) {
            Future.value(http.Response(request.version, http.Status.Ok))
        } else {
            Future.value(http.Response(request.version, http.Status.Unauthorized))
        }
    }
}


object Main extends App {

    // for other Request
//    val blankService = new Service[Request, Response] {
//        def apply(request: http.Request): Future[http.Response] = {
//            val rep = http.Response(request.version, http.Status.BadRequest)
//            rep.setContentString("finagle")
//            Future.value(rep)
//        }
//    }
//    val sampleFilter = new SampleFilter
//    val test = sampleFilter andThen blankService
//
//    val router = RoutingService.byPathObject[Request] {
//        case Root / "test" => test
//        case _                                   => blankService
//    }
//
//    val server = Http.serve(s":9000", router)
//    Await.ready(server)

    RedisCluster.start(1)
    val client = Client("127.0.0.1:6379")
    println("Setting foo -> bar...")
    client.set(StringToChannelBuffer("foo"), StringToChannelBuffer("bar"))
    println("Getting value for key 'foo'")
    val getResult = Await.result(client.get(StringToChannelBuffer("foo")))
    getResult match {
        case Some(n) => println("Got result: " + new String(n.array))
        case None => println("Didn't get the value!")
    }

    println("Closing client...")
    client.close()
    println("Stopping Redis instance...")
    println("Done!")
}
