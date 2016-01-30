package com.lopez83.qstart.jaxrs;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("/hello")
public class HelloWorld {

	@GET
	@Path("/echo/{input}")
	@Produces("text/plain")
	public String ping(@PathParam("input") String input) {
		return input;
	}

	@GET
	@Path("/printjson/{input}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response printJson(@PathParam("input") String input) {
		// return input;
		JsonBean bean = new JsonBean();
		bean.setVal1(input);
		bean.setVal2("hardcoded");
		return Response.ok().entity(bean).build();
	}

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	@Path("/jsonBean")
	public Response modifyJson(JsonBean input) {
		input.setVal2(input.getVal1());
		return Response.ok().entity(input).build();
	}

	@POST
	@Consumes("application/x-www-form-urlencoded")
	public void post(@FormParam("name") String name) {
		// Store the message
	}

	@POST
	@Consumes("application/x-www-form-urlencoded")
	public void post(MultivaluedMap<String, String> formParams) {
		// Store the message
	}

	//conditional cache
	@Path("/jsonbean/{name}")
	@GET
	public Response getBook(@PathParam("name") String name,
			@Context Request request) {

		JsonBean bean = new JsonBean();
		bean.setVal1(name);
		bean.setVal2("hardcoded");

		CacheControl cc = new CacheControl();
		cc.setMaxAge(86400);

		EntityTag etag = new EntityTag(Integer.toString(bean.hashCode()));
		ResponseBuilder builder = request.evaluatePreconditions(etag);

		// cached resource did change -> serve updated content
		if (builder == null) {
			builder = Response.ok(bean);
			builder.tag(etag);
		}

		builder.cacheControl(cc);
		return builder.build();
	}

}
