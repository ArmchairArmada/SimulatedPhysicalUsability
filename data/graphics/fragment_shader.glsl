#version 330

uniform sampler2D texture0;

in vec3 theNormal;
in vec3 theColor;
in vec2 theUv;

out vec4 outColor;

void main() {
    float specularPower = 25.0;
    float specularIntensity = 0.25;
    vec3 materialColor = vec3(0.1, 0.35, 0.8);
    vec3 lightDirection = normalize(vec3(1.0, 2.0, 3.0));
    float diffuse = max(dot(lightDirection, theNormal), 0.0);
    float specular = clamp(pow(max(dot(lightDirection, theNormal), 0.0), specularPower)*specularIntensity, 0.0, 1.0);
    float ambient = dot(theNormal, vec3(0, 1, 0))/2.0 + 0.5;
    float rim = clamp((1.0-pow(dot(theNormal, vec3(0, 0, 1))+0.5, 3.0))*0.75, 0.0, 1.0);
    
    //outColor = vec4((theColor + diffuse) * materialColor + specular, 1.0);
    
    float c = 0.0; //mod(floor((theUv.x + theUv.y) * 100),2);
    
    materialColor = texture2D(texture0, theUv).rgb;
    outColor = vec4((theColor + ambient + diffuse) * min(c*0.25+materialColor,1.0) + specular + ambient*rim, 1.0);
}
