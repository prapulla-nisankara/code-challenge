package com.mastercard.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Prapulla Nisankara
 *
 */
@RestController
public class HelloController {

	@RequestMapping("/")
	public String index() {
		return "check cities connected ex: http://localhost:8080/connected?origin=Boston&destination=Newark";
	}

}
