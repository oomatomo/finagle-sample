package com.oomatomo.finaglesample

import com.twitter.finagle._
import com.twitter.finagle.http.{ Request, Response }
import com.twitter.finagle.http.service.RoutingService
import com.twitter.finagle.http.path._
import com.twitter.util.{ Await, Future }

object Main extends App {

    // for other Request
    val blankService = new Service[Request, Response] {
        def apply(request: http.Request): Future[http.Response] = {
            val rep = http.Response(request.version, http.Status.BadRequest)
            rep.setContentString("finagle")
            Future.value(rep)
        }
    }

    val router = RoutingService.byPathObject[Request] {
        case _                                   => blankService
    }

    val server = Http.serve(s":9000", router)
    Await.ready(server)
}
