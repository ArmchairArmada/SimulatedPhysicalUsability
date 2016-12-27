#version 330

in layout(location=0) vec3 position;
in layout(location=2) vec3 inColor;

out vec3 theColor;

void main() {
    gl_Position = vec4(position, 1.0);
    theColor = inColor;
}
