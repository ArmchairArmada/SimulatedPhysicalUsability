#version 330

in vec3 position;
in vec3 normal;
/* in vec3 color; */
in vec2 uv;

uniform vec3 ambient;

uniform vec4 diffuseColor;
uniform vec4 specularColor;
uniform float specularPower;
uniform float specularIntensity;

uniform mat4 modelView;
uniform mat4 projection;

out vec3 theNormal;
out vec2 theUv;
out vec3 theColor;

out vec4 theDiffuseColor;
out vec4 theSpecularColor;
out float theSpecularPower;
out float theSpecularIntensity;

void main() {
    gl_Position = projection * modelView * vec4(position, 1.0);
    theNormal = (modelView * vec4(normal, 0.0)).xyz;
    theUv = uv;
    theColor = /* color * */ ambient;
    
    theDiffuseColor = diffuseColor;
    theSpecularColor = specularColor;
    theSpecularPower = specularPower;
    theSpecularIntensity = specularIntensity;
}
