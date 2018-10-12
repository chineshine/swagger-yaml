/**
 * 
 */
package c.s.swagger.yaml;

import lombok.Data;

/**
 * @author chineshine
 *
 */
@Data
public class PathField {

	private String name;

	private String in;

	private Boolean required;

	private String type;

	private Boolean hasRef;
	
	private String refClassName;

}
