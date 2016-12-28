#version 330

in layout(location=0) vec3 position;
in layout(location=1) vec3 color;

uniform mat4 mvp;

out vec3 theColor;

void main() {
    gl_Position = mvp * vec4(position, 1.0);
    theColor = color;
}
