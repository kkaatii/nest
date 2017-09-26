package photon.rest;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import photon.tube.model.ArrowType;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

@ControllerAdvice("photon.api")
public class ApiControllerAdvice {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        PropertyEditor arrowTypeEditor = new PropertyEditorSupport() {
            @Override
            public String getAsText() {
                return getValue().toString();
            }

            @Override
            public void setAsText(String atString) throws IllegalArgumentException {
                setValue(ArrowType.extendedValueOf(atString));
            }
        };
        binder.registerCustomEditor(ArrowType.class, arrowTypeEditor);
    }

}