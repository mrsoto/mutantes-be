package me.mrs.mutantes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/docs/*")
public class DocsResource {

    @GET
    public String docs() {
        return "/docs/index.html";
    }
}
