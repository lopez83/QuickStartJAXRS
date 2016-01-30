package com.lopez83.qstart.jaxrs;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.container.TimeoutHandler;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.lopez83.qstart.jaxrs.beans.UserBean;

@Path("/users")
public class UserRestService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public UserBean getUserInJSON() {

		UserBean user = new UserBean();
		user.setName("Oscar");
		user.setSurname("Lopez");

		return user;

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createUserInJSON(UserBean user) {

		String result = "User saved : " + user;
		return Response.status(201).entity(result).build();

	}

	@GET
	@Path("{username: [a-zA-Z][a-zA-Z_0-9]*}")
	public Response getUserByUsername(@PathParam("username") String username) {

		return Response.status(200)
				.entity("getUserByUsername is called, username : " + username)
				.build();
	}

	@GET
	@Path("{id : \\d+}")
	// support digit only
	public Response getUserById(@PathParam("id") String id) {

		return Response.status(200).entity("getUserById is called, id : " + id)
				.build();

	}

	@GET
	@Path("{year}/{month}/{day}")
	public Response getUserHistory(@PathParam("year") int year,
			@PathParam("month") int month, @PathParam("day") int day) {

		String date = year + "/" + month + "/" + day;

		return Response.status(200)
				.entity("getUserHistory is called, year/month/day : " + date)
				.build();

	}

	// users/query?from=100&to=200&orderBy=age&orderBy=name
	@GET
	@Path("/query")
	public Response getUsers(
			@DefaultValue("1000") @QueryParam("from") int from,
			@QueryParam("to") int to,
			@QueryParam("orderBy") List<String> orderBy) {

		return Response
				.status(200)
				.entity("getUsers is called, from : " + from + ", to : " + to
						+ ", orderBy" + orderBy.toString()).build();

	}

	@GET
	@Path("/async")
	// in case of a request a thread is only responsible to accept the request
	// and put it into a processing queue. The Java method that handles the
	// request terminates almost immediately.
	public void asyncGet(@Suspended final AsyncResponse asyncResponse) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				Response result = veryExpensiveOperation();
				asyncResponse.resume(result);
			}

			private Response veryExpensiveOperation() {

				try {
					Thread.sleep(5 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				return Response.status(200).entity("async get finished")
						.build();
			}
		}).start();
	}

	@GET
	@Path("/asynctime")
	public void asyncGetWithTimeout(@Suspended final AsyncResponse asyncResponse) {
		asyncResponse.setTimeoutHandler(new TimeoutHandler() {

			@Override
			public void handleTimeout(AsyncResponse asyncResponse) {
				asyncResponse.resume(Response
						.status(Response.Status.SERVICE_UNAVAILABLE)
						.entity("Operation time out.").build());
			}
		});
		asyncResponse.setTimeout(3, TimeUnit.SECONDS);

		new Thread(new Runnable() {

			@Override
			public void run() {
				Response result = veryExpensiveOperation();
				asyncResponse.resume(result);
			}

			private Response veryExpensiveOperation() {
				try {
					Thread.sleep(5 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				return Response.status(200).entity("async get finished")
						.build();
			}
		}).start();
	}
}