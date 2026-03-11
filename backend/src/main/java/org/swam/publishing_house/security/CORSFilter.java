package org.swam.publishing_house.security;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
@PreMatching
public class CORSFilter implements ContainerRequestFilter, ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// Handle preflight OPTIONS requests
		if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
			requestContext.abortWith(
					Response.ok()
							.header("Access-Control-Allow-Origin", "*")
							.header("Access-Control-Allow-Headers", "*")
							.header("Access-Control-Allow-Methods", "*")
							.header("Access-Control-Max-Age", "3600")
							.build());
		}
	}

	@Override
	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) throws IOException {

		MultivaluedMap<String, Object> headers = responseContext.getHeaders();

		// Allow everything - perfect for localhost development
		headers.putSingle("Access-Control-Allow-Origin", "*");
		headers.putSingle("Access-Control-Allow-Headers", "*");
		headers.putSingle("Access-Control-Allow-Methods", "*");
		headers.putSingle("Access-Control-Max-Age", "3600");
	}
}