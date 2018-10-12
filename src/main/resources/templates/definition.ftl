  ${classname}:
    type: object
    properties:
    <#list fields as field>
      ${field.name}: 
        type: ${field.type}
        description: 
        <#if field.hasRef>
        $ref: '#/definitions/${field.refClassName}'
        </#if>
    </#list>
