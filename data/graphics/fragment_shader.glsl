#version 330

in vec3 theNormal;
in vec3 theColor;
in vec3 theUv;

out vec4 outColor;

void main() {
    vec3 lightDirection = normalize(vec3(1.0, 2.0, 3.0));
    float diffuse = max(dot(lightDirection, theNormal), 0.0);
    float specular = clamp(pow(max(dot(lightDirection, theNormal), 0.0), 25.0)*0.75, 0.0, 1.0);
    outColor = vec4((theColor + diffuse) * vec3(0.2, 0.5, 0.7) + specular, 1.0);
}
