#version 330

in layout(location=0) vec3 position;
in layout(location=1) vec3 normal;
in layout(location=2) vec3 color;
in layout(location=3) vec2 uv;

uniform mat4 mvp;

out vec3 theNormal;
out vec2 theUv;
out vec3 theColor;

void main() {
    gl_Position = mvp * vec4(position, 1.0);
    theNormal = normal;
    theUv = uv;
    theColor = color;
}
