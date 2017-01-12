#version 330

%include partial_fragment_base.glsl

in vec3 theNormal;
in vec3 theColor;

in vec4 theDiffuseColor;
out vec4 outColor;

void main() {
    vec4 ambient = vec4(0.5, 0.5, 0.5, 0.5);
    vec3 lightDirection = normalize(vec3(1.0, 2.0, 3.0));
    float diffuse = max(dot(lightDirection, theNormal), 0.0);

    outColor = mixFog((ambient + diffuse) * theDiffuseColor);
}
