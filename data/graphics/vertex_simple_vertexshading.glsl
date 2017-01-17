#version 330

%include partial_vertex_base.glsl

in vec3 position;
in vec3 normal;

uniform vec4 diffuseColor;

uniform mat4 modelView;
uniform mat4 projection;

vec3 theNormal;

out vec4 theColor;

void main() {
    gl_Position = projection * modelView * vec4(position, 1.0);
    theNormal = (modelView * vec4(normal, 0.0)).xyz;
    
    vec4 ambient = vec4(0.5, 0.5, 0.5, 0.5);
    //vec3 lightDirection = normalize(vec3(1.0, 2.0, 3.0));
    vec3 lightDirection = vec3(0.26726124191242438468, 0.53452248382484876937, 0.80178372573727315405);
    float diffuse = max(dot(lightDirection, theNormal), 0.0);

    theColor = mixFog(gl_Position, (ambient + diffuse) * diffuseColor);
}
