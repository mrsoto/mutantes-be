package me.mrs.mutantes;

import com.google.inject.AbstractModule;
import me.mrs.mutantes.annotaion.PersistenceRetryMs;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class WebModule extends AbstractModule {
    private static Validator validatorSupplier() {
        var validatorFactory = Validation.buildDefaultValidatorFactory();
        return validatorFactory.getValidator();
    }

    @Override
    protected void configure() {
        bind(Validator.class).toProvider(WebModule::validatorSupplier);
        bind(ModelMapper.class).to(ModelMapper.class);
        bind(BlockingQueue.class).toProvider(() -> new ArrayBlockingQueue<>(10));
        bindConstant().annotatedWith(PersistenceRetryMs.class).to(100L);
    }

}
