package com.mediatica.onlinebanking.generics;
import java.lang.reflect.*;
import org.springframework.data.jpa.repository.JpaRepository;

//This generic class will be used for the 'update' operation of a record in the API.
//Reusability of the code inside the 'updateRecord()' method makes this special.
//We don't have to explicitly write the code individually for each service where it will be used.
//So, despite of the model class  and the repository instance that will be used as arguments of the constructor, the record will be updated independently and accordingly.
public class RecordUpdate<T, V, L extends JpaRepository<T, Integer>>
{
    private T existingRecord;
    private V modelInstance;
    private L repositoryInstance;

    public RecordUpdate(T existingRecord, V modelInstance, L repositoryInstance)
    {
        this.existingRecord = existingRecord;
        this.modelInstance = modelInstance;
        this.repositoryInstance = repositoryInstance;
    }

    public T updateRecord()
    {
        // Get the class of the 'Model' object, which may be a 'Card' or 'Account' type.
        Class<?> existingRecordClass = existingRecord.getClass();

        // This loop is used to programatically set the updated values of each field, using their getter and setter methods.
        for(Field field : existingRecordClass.getDeclaredFields())
        {
            try
            {
                // Get the name of the field
                String fieldName = field.getName();

                // Create a getter method name based on the field name
                String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                // Find the getter method
                Method getterMethod = existingRecordClass.getMethod(getterName);

                // Get the value of the field from the 'card' object
                Object value = getterMethod.invoke(modelInstance);

                // If the value is not null, update the corresponding field in 'existingAccount'
                if (value != null)
                {
                    // Create a setter method name based on the field name
                    String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                    // Find the setter method
                    Method setterMethod = existingRecordClass.getMethod(setterName, field.getType());

                    // Set the field value in 'existingAccount'
                    setterMethod.invoke(existingRecord, value);
                }
            }

            catch (Exception e)
            {
                // Handle any reflection-related exceptions
                e.printStackTrace();
            }
        }

        // Save the updated 'existingAccount'
        T updatedRecord = repositoryInstance.save(existingRecord);
        if(updatedRecord != null)
            return updatedRecord;
        else
            return null;

    }


    public static void main(String[] args)
    {}
}