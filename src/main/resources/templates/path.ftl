  ${uri}: 
    ${method}: 
      summary: 
      description: >
      
      <#if hasParameters>
      parameters: 
      <#list fields as field>
        - name: ${field.name}
          in: ${field.in}
          description: 
          required: ${field.required?c}
          schema:
            $ref: '#/definitions/${field.type}'
      </#list>  
      </#if>
      tags: 
      	
      responses: 
        200: 
          description: 
          schema: 
            <#if isPageabled>
            type: object
            properties: 
			  totalElements:
                type: integer
                description: 数据总个数
              size:
                type: integer
                description: 当前页面个数
              content:
                type: array
                items:
                  $ref: '#/definitions/${responseType}'
            <#elseif isList>
            type: array
            items: 
              $ref: "#/definitions/${responseType}"
            <#elseif isObject>
            $ref: "#/definitions/${responseType}"
			<#else>
			type: ${responseType}
			</#if>
              
      
