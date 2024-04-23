package org.jarreto.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jarreto.service.QueryProcessorService;

@Path("/processor")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class QueryProcessorResource {

    private final QueryProcessorService service;

    public QueryProcessorResource(QueryProcessorService service) {
        this.service = service;
    }

    @POST
    public Boolean processor(String query) {
        return service.queryProcessor(query);
    }

}
