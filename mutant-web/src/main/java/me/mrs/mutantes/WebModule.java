package me.mrs.mutantes;

import com.google.inject.AbstractModule;

import javax.validation.Validation;
import javax.validation.Validator;

public class WebModule extends AbstractModule {
    private static Validator validatorSupplier() {
        var validatorFactory = Validation.buildDefaultValidatorFactory();
        return validatorFactory.getValidator();
    }

    @Override
    protected void configure() {
        bind(Validator.class).toProvider(WebModule::validatorSupplier);
        bind(ModelMapper.class).to(ModelMapper.class);
    }

}
