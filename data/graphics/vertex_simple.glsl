#version 330

in vec3 position;
in vec3 normal;

uniform vec4 diffuseColor;

uniform mat4 modelView;
uniform mat4 projection;

out vec3 theNormal;
out vec4 theDiffuseColor;

void main() {
    gl_Position = projection * modelView * vec4(position, 1.0);
    theNormal = (modelView * vec4(normal, 0.0)).xyz;
    theDiffuseColor = diffuseColor;
}
