/**
 * 
 */
package c.s.swagger.example;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chineshine
 *
 */
@RestController
@RequestMapping("/example")
public class ExampleController {

	@PostMapping
	public ResponseEntity<Boolean> save(@RequestBody ExampleVo vo){
		return ResponseEntity.ok(false);
	}
	
	@GetMapping("/page")
	public ResponseEntity<Page<ExampleVo>> page(){
		return null;
	}
}
