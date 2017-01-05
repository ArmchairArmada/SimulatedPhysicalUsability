#version 330

in vec3 position;
in vec2 uv;

uniform mat4 modelView;
uniform mat4 projection;

out vec2 theUv;

void main() {
    gl_Position = projection * modelView * vec4(position, 1.0);
    theUv = uv;
}
