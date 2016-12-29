#version 330

in vec3 position;
in vec3 normal;
/* in vec3 color; */
in vec2 uv;

uniform vec3 ambient;
uniform vec3 diffuse;
uniform vec3 specular;
uniform float hardness;

uniform mat4 modelView;
uniform mat4 projection;

out vec3 theNormal;
out vec2 theUv;
out vec3 theColor;

void main() {
    gl_Position = projection * modelView * vec4(position, 1.0);
    theNormal = (modelView * vec4(normal, 0.0)).xyz;
    theUv = uv;
    theColor = /* color * */ ambient;
}
