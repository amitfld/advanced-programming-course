
function solution_js_query(){
    let elements = document.querySelectorAll("#q5 a.malicious");
    elements.forEach ((el) => {
        el.setAttribute("style", "display:none");
    });
    let div_block = document.querySelector("#q5 div.hidden");
    div_block.setAttribute("style", "display:block")
}

function solution_js_dynamic_elements(){
    let new_div_element = document.createElement("div");
    let new_h2_element = document.createElement("h2");
    let new_p_element = document.createElement("p");
    let parent = document.getElementById("q6");
    new_p_element.textContent = "This is a paragraph";
    new_h2_element.textContent = "This id header2";
    new_div_element.setAttribute("id", "newDiv");
    parent.appendChild(new_div_element);
    new_div_element.appendChild(new_h2_element);
    new_div_element.appendChild(new_p_element);    
}

function solution_js_event_listeners(){
    document.getElementById("div_btn").addEventListener("click", function(){ alert("click")});
    document.body.addEventListener("keydown", (key_pressed) => {alert(`The key '${key_pressed.key}' was pressed`)});
}

function solution_js_unit_converter(){
    let from_unit = document.getElementById("convert_from_unit").value;
    let to_unit = document.getElementById("convert_to_unit").value;
    let user_input = parseFloat(document.getElementById("convertion_input").value);
    let output = document.getElementById("convertion_output");
    let comnverted_value;
    if (from_unit === 'cm'){
        if (to_unit === 'cm'){comnverted_value = user_input}
        if (to_unit === 'meter'){comnverted_value = user_input / 100}
        if (to_unit === 'inch'){comnverted_value = user_input / 2.54}
        if (to_unit === 'foot'){comnverted_value = user_input / 30.48}
    }
    if (from_unit === 'meter'){
        if (to_unit === 'cm'){comnverted_value = user_input * 100}
        if (to_unit === 'meter'){comnverted_value = user_input}
        if (to_unit === 'inch'){comnverted_value = user_input * 39.3700787}
        if (to_unit === 'foot'){comnverted_value = user_input * 3.28084}
    }
    if (from_unit === 'inch'){
        if (to_unit === 'cm'){comnverted_value = user_input * 2.54}
        if (to_unit === 'meter'){comnverted_value = user_input / 39.37}
        if (to_unit === 'inch'){comnverted_value = user_input}
        if (to_unit === 'foot'){comnverted_value = user_input / 12}
    }
    if (from_unit === 'foot'){
        if (to_unit === 'cm'){comnverted_value = user_input * 30.48}
        if (to_unit === 'meter'){comnverted_value = user_input / 3.281}
        if (to_unit === 'inch'){comnverted_value = user_input * 12}
        if (to_unit === 'foot'){comnverted_value = user_input}
    }
    output.value = comnverted_value.toFixed(12);
}

function validate_form(){
    let valid_username = validate_username();
    let valid_password = validate_password();
    let valid_email = validate_email();
    let valid_age = validate_age();
    if (valid_username && valid_password && valid_email && valid_age){
        alert("The form is valid");
    } else {
        alert("The form is invalid");
    }
}

function validate_username(){
    let username = document.getElementById("username").value;
    if (username.length < 4){
        return false;
    }
    return /^[a-zA-Z0-9-]+$/.test(username);

}

function validate_password(){
    let password = document.getElementById("password").value;
    if (password.length < 8){
        return false;
    }
    return /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*\-_\(\)]).+$/.test(password);

}

function validate_email(){
    let email = document.getElementById("email").value;
    if (email.includes("#") || email.includes("..") || !email.includes("@")) {
        return false;
    }
    if (email.split("@").length > 2){
        return false;
    }
    let user_part = email.split('@')[0];
    let domain_part = email.split('@')[1];
    if (user_part.length < 1){
        return false;
    }
    if (user_part.startsWith(".") || user_part.startsWith("-") || user_part.endsWith(".") || user_part.endsWith("-")) {
        return false;
    }
    let segments = domain_part.split("."); 
    if (segments.length < 2) {
        return false;
    }
    let root_domain = `${segments[segments.length - 1]}`;
    if (root_domain.length < 2) {
        return false;
    }
    return true;
}

function validate_age(){
    let age = document.getElementById("age").value;
    return age >= 10 && age <= 120;
    
}

