#version 330

in vec3 theNormal;
in vec3 theColor;

in vec4 theDiffuseColor;
out vec4 outColor;

void main() {
    vec4 ambient = vec4(0.5, 0.5, 0.5, 0.5);
    vec3 lightDirection = normalize(vec3(1.0, 2.0, 3.0));
    float diffuse = max(dot(lightDirection, theNormal), 0.0);
    float fog = clamp(pow(gl_FragCoord.z/gl_FragCoord.w/240.0, 1.5), 0.0, 1.0);
    
    outColor = mix((ambient + diffuse) * theDiffuseColor, vec4(1,1,1,1), fog);
}
