package udf;

import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class EncryptPhoneNumberTest {
    @Test
    public void testEncryptPhoneNumber() throws HiveException {
        EncryptPhoneNumber udf = new EncryptPhoneNumber();
        ObjectInspector[] inputOIs = {PrimitiveObjectInspectorFactory.javaStringObjectInspector};
        udf.initialize(inputOIs);

        // 测试用例：正常手机号
        GenericUDF.DeferredObject[] args = {new GenericUDF.DeferredJavaObject("13812345678")};
        String result = (String) udf.evaluate(args);
        Assertions.assertEquals("138****5678", result);

        // 测试用例：不合法的手机号
        args = new GenericUDF.DeferredObject[]{new GenericUDF.DeferredJavaObject("123456")};
        result = (String) udf.evaluate(args);
        Assertions.assertEquals("123456", result);
    }
}
