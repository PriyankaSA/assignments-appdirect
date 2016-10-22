package com.appdirect;

import com.appdirect.dto.subscription.Notification;
import com.appdirect.mongo.documents.SubscriptionRecord;
import com.appdirect.mongo.documents.UserRecord;
import com.appdirect.web.SubscriptionController;
import com.appdirect.web.commons.ErrorResponse;
import com.appdirect.web.commons.Response;
import com.appdirect.web.commons.SuccessResponse;
import junit.framework.Assert;
import org.apache.commons.codec.binary.Base64;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int randomServerPort;

	private static HttpEntity<String> request;

	@BeforeClass
	public static void setUpTest(){
		String plainCreds = "user:password";
		String base64Creds = new String(Base64.encodeBase64(plainCreds.getBytes()));
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);
		request = new HttpEntity<>(headers);
	}

	@Test
	public void testSubscriptionFlow() {
		final String createSubscriptionUrl = "http://localhost:" + randomServerPort + "/proxy/api/events/createSubscription";
		final String changeSubscriptionUrl = "http://localhost:" + randomServerPort + "/proxy/api/events/changeSubscription?accountId=";
		final String cancelSubscriptionUrl = "http://localhost:" + randomServerPort + "/proxy/api/events/cancelSubscription?accountId=";
		ResponseEntity<SuccessResponse> successResponse;
		ResponseEntity<ErrorResponse> errorResponse;
		String createdAccountId;

		//Create Subscription
		successResponse = this.restTemplate.exchange("/api/subscription/create?url=" + createSubscriptionUrl, HttpMethod.GET, request, SuccessResponse.class);
		assertEquals(HttpStatus.OK, successResponse.getStatusCode());
		assertTrue(successResponse.getBody().getSuccess());
		assertNotNull(successResponse.getBody().getAccountIdentifier());
		createdAccountId = successResponse.getBody().getAccountIdentifier();
		//User Already exists error
		errorResponse = this.restTemplate.exchange("/api/subscription/create?url=" + createSubscriptionUrl, HttpMethod.GET, request, ErrorResponse.class);
		assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
		assertFalse(errorResponse.getBody().getSuccess());
		assertEquals(ErrorResponse.ErrorCode.USER_ALREADY_EXISTS, errorResponse.getBody().getErrorCode());

		assertEquals(1, getSubscriptionLists().size());

		//Change Subscription
		successResponse = this.restTemplate.exchange("/api/subscription/change?url=" + changeSubscriptionUrl + createdAccountId, HttpMethod.GET, request, SuccessResponse.class);
		assertEquals(HttpStatus.OK, successResponse.getStatusCode());
		assertTrue(successResponse.getBody().getSuccess());
		assertNotNull(successResponse.getBody().getAccountIdentifier());
		//Account not found error
		errorResponse = this.restTemplate.exchange("/api/subscription/change?url=" + changeSubscriptionUrl + "1", HttpMethod.GET, request, ErrorResponse.class);
		assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
		assertFalse(errorResponse.getBody().getSuccess());
		assertEquals(ErrorResponse.ErrorCode.ACCOUNT_NOT_FOUND, errorResponse.getBody().getErrorCode());

		assertEquals(1, getSubscriptionLists().size());
		assertEquals(1, getUserLists(createdAccountId).size());

		//Cancel Subscription
		successResponse = this.restTemplate.exchange("/api/subscription/cancel?url=" + cancelSubscriptionUrl + createdAccountId, HttpMethod.GET, request, SuccessResponse.class);
		assertEquals(HttpStatus.OK, successResponse.getStatusCode());
		assertTrue(successResponse.getBody().getSuccess());
		assertNotNull(successResponse.getBody().getAccountIdentifier());
		//Account not found error
		errorResponse = this.restTemplate.exchange("/api/subscription/cancel?url=" + cancelSubscriptionUrl + "1", HttpMethod.GET, request, ErrorResponse.class);
		assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
		assertFalse(errorResponse.getBody().getSuccess());
		assertEquals(ErrorResponse.ErrorCode.ACCOUNT_NOT_FOUND, errorResponse.getBody().getErrorCode());

		assertEquals(0, getSubscriptionLists().size());
		assertEquals(0, getUserLists(createdAccountId).size());

	}

	@Test
	public void testUserFlow() {
		final String createSubscriptionUrl = "http://localhost:" + randomServerPort + "/proxy/api/events/createSubscription";
		final String userAssignmentUrl = "http://localhost:" + randomServerPort + "/proxy/api/events/userAssignment?accountId=";
		final String userUnassignmentUrl = "http://localhost:" + randomServerPort + "/proxy/api/events/userUnassignment?accountId=";
		ResponseEntity<SuccessResponse> successResponse;
		ResponseEntity<ErrorResponse> errorResponse;
		String createdAccountId;

		successResponse = this.restTemplate.exchange("/api/subscription/create?url=" + createSubscriptionUrl, HttpMethod.GET, request, SuccessResponse.class);
		createdAccountId = successResponse.getBody().getAccountIdentifier();

		assertEquals(1, getSubscriptionLists().size());
		assertEquals(1, getUserLists(createdAccountId).size());

		//User Assignment
		successResponse = this.restTemplate.exchange("/api/user/assign?url=" + userAssignmentUrl + createdAccountId, HttpMethod.GET, request, SuccessResponse.class);
		assertEquals(HttpStatus.OK, successResponse.getStatusCode());
		assertTrue(successResponse.getBody().getSuccess());
		assertNotNull(successResponse.getBody().getAccountIdentifier());
		//Account not found error
		errorResponse = this.restTemplate.exchange("/api/user/assign?url=" + userAssignmentUrl + "1", HttpMethod.GET, request, ErrorResponse.class);
		assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
		assertFalse(errorResponse.getBody().getSuccess());
		assertEquals(ErrorResponse.ErrorCode.ACCOUNT_NOT_FOUND, errorResponse.getBody().getErrorCode());

		assertEquals(2, getUserLists(createdAccountId).size());

		//User Unassignment
		successResponse = this.restTemplate.exchange("/api/user/unassign?url=" + userUnassignmentUrl + createdAccountId, HttpMethod.GET, request, SuccessResponse.class);
		assertEquals(HttpStatus.OK, successResponse.getStatusCode());
		assertTrue(successResponse.getBody().getSuccess());
		assertNotNull(successResponse.getBody().getAccountIdentifier());
		//Account not found error
		errorResponse = this.restTemplate.exchange("/api/user/unassign?url=" + userUnassignmentUrl + "1", HttpMethod.GET, request, ErrorResponse.class);
		assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
		assertFalse(errorResponse.getBody().getSuccess());
		assertEquals(ErrorResponse.ErrorCode.ACCOUNT_NOT_FOUND, errorResponse.getBody().getErrorCode());

		assertEquals(1, getUserLists(createdAccountId).size());

	}

	private List<SubscriptionRecord> getSubscriptionLists(){
		ResponseEntity<List<SubscriptionRecord>> response = this.restTemplate.exchange("/api/subscription", HttpMethod.GET, request, new ParameterizedTypeReference<List<SubscriptionRecord>>(){});
		return response.getBody();
	}

	private List<UserRecord> getUserLists(String accountId){
		ResponseEntity<List<UserRecord>> response = this.restTemplate.exchange("/api/user/"+accountId, HttpMethod.GET, request, new ParameterizedTypeReference<List<UserRecord>>(){});
		return response.getBody();
	}
}
