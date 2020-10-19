package com.mastercard.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
public class CityConnectTest {
	@Autowired
	private MockMvc mvc;
	
	@Test
	@Order(1)
	public void citiesConnectedTest() throws Exception
	{
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/connected?origin=Boston&destination=Newark")).andReturn();		
		assertEquals(200, mvcResult.getResponse().getStatus());
		
		String content = mvcResult.getResponse().getContentAsString();
		assertEquals("Yes", content);
	}
	
	@Test
	@Order(2)
	public void citiesNotConnectedTest() throws Exception
	{
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/connected?origin=Boston&destination=Toronto")).andReturn();		
		assertEquals(200, mvcResult.getResponse().getStatus());
		
		String content = mvcResult.getResponse().getContentAsString();
		assertEquals("No", content);
	}
	
	@Test
	@Order(3)
	public void noCityInputTest() throws Exception
	{
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/connected?origin=&destination=")).andReturn();		
		assertEquals(200, mvcResult.getResponse().getStatus());
		
		String content = mvcResult.getResponse().getContentAsString();
		assertEquals("No", content);
	}
	
	@Test
	@Order(4)
	public void inValidUrlTest() throws Exception
	{
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/connected?origin=&destination=")).andReturn();		
		assertEquals(200, mvcResult.getResponse().getStatus());
		
		String content = mvcResult.getResponse().getContentAsString();
		assertEquals("No", content);
	}
	
}
