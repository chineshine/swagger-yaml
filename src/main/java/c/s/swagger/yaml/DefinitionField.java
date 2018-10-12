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
public class DefinitionField {

	private String name;
	
	private String type;
	
	private Boolean hasRef;
	
	private String refClassName;
}
