package udf;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Description(
    name = "encrypt_phone_number",
    value = "_FUNC_(String) - Returns String",
    extended = "实现对手机号中间4位进行****加密\n" +
            "Example: \n" +
            ">SELECT _FUNC_(String) FROM src LIMIT 1;\n"
)


public class EncryptPhoneNumber extends GenericUDF {

    // 0. ObjectInspector，通常以成员变量的形式被创建
    private StringObjectInspector stringOI;

    public EncryptPhoneNumber() {
    }

    @Override
    public ObjectInspector initialize(ObjectInspector[] args) throws UDFArgumentException {
        // 1. 检查该记录是否传过来正确的参数数量
        if (args.length !=1 ) {
            throw new UDFArgumentLengthException(
                    "The operator 'encrypt_phone_number' accepts 1 args.");
        }
        // 2. 检查该条记录是否传过来正确的参数类型
        if(!(args[0] instanceof StringObjectInspector)) {
            throw new UDFArgumentTypeException(
                    1, "The data type of function argument should be string");
        }

        // 3. 检查通过后，将参数赋值给成员变量ObjectInspector，为了在evaluate()中使用
        this.stringOI = (StringObjectInspector) args[0];
        // this.elementOI = (StringObjectInspector) args[1];

        // 4. 用工厂类生成用于表示返回值类型的 ObjectInspector（Java的String）
        PrimitiveObjectInspector.PrimitiveCategory itemType = PrimitiveObjectInspector.PrimitiveCategory.STRING;
        return PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(itemType);
    }

    @Override
    public Object evaluate(DeferredObject[] args) throws HiveException {
        // get the string from the deferred objects using the object inspectors
        String phoNum = this.stringOI.getPrimitiveJavaObject(args[0].get());

        // 业务逻辑
        String encryptPhoNum = null;

        //手机号不为空 并且为11位
        if (StringUtils.isNotEmpty(phoNum) && phoNum.trim().length() == 11 ) {
            //判断数据是否满足中国大陆手机号码规范
            String regex = "^(1[3-9]\\d{9}$)";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phoNum);
            if (m.matches()) {
                //进入这里都是符合手机号规则的：使用正则替换 返回加密后数据
                encryptPhoNum = phoNum.trim().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
            }else{
                //不符合手机号规则 数据直接原封不动返回
                encryptPhoNum = phoNum;
            }
        }else{
            //不符合11位 数据直接原封不动返回
            encryptPhoNum = phoNum;
        }
        return encryptPhoNum;
    }

    @Override
    public String getDisplayString(String[] arg0) {
        return "EncryptPhoneNumber()"; // this should probably be better
    }
}
