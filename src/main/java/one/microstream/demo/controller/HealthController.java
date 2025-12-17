package one.microstream.demo.controller;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.exceptions.HttpStatusException;

@Controller("/health")
public class HealthController
{
    private boolean ready = true;
    private boolean healthy = true;

    @Get
    public void health()
    {
        if (!this.healthy)
        {
            throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "healthy=false");
        }
    }

    @Put("/{healthy}")
    public void health(boolean healthy)
    {
        this.healthy = healthy;
    }

    @Get("/ready")
    public void ready()
    {
        if (!this.ready)
        {
            throw new HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ready=false");
        }
    }

    @Put("/health/ready/{ready}")
    public void ready(boolean ready)
    {
        this.ready = ready;
    }
}
