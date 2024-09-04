package udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

import java.util.ArrayList;
import java.util.List;


@Description(
        name    =   "cast_array_item",
        value   =   "_FUNC_(array<>) - Returns array<String>",
        extended=   "Example: \n" +
                ">SELECT _FUNC_(array(integer)) FROM src LIMIT 1;\n"
)


public class CastArrayItem extends GenericUDF {

    // 0. ObjectInspector，通常以成员变量的形式被创建
    private ListObjectInspector listOI;
    // private StringObjectInspector elementOI;

    public CastArrayItem() {
    }

    @Override
    public ObjectInspector initialize(ObjectInspector[] args) throws UDFArgumentException {
        // 1. 检查该记录是否传过来正确的参数数量
        if (args.length !=1 ) {
            throw new UDFArgumentLengthException(
                    "The operator 'cast_array_item' accepts 1 args.");
        }
        // 2. 检查该条记录是否传过来正确的参数类型
        if(!(args[0] instanceof ListObjectInspector)) {
            throw new UDFArgumentTypeException(
                    1, "The data type of function argument should be array<>");
        }

        // 3. 检查通过后，将参数赋值给成员变量ObjectInspector，为了在evaluate()中使用
        this.listOI = (ListObjectInspector) args[0];
        // this.elementOI = (StringObjectInspector) args[1];

        // 4. 用工厂类生成用于表示返回值的ObjectInspector（Java的list）
        PrimitiveObjectInspector.PrimitiveCategory itemType = PrimitiveObjectInspector.PrimitiveCategory.STRING;
        ObjectInspector returnOi = PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(itemType);
        return ObjectInspectorFactory.getStandardListObjectInspector(returnOi);
    }

    @Override
    public Object evaluate(DeferredObject[] args) throws HiveException {
        // get the list and string from the deferred objects using the object inspectors
        List<Object> items = (List<Object>) this.listOI.getList(args[0].get());

        // check for nulls
        if (items == null) {
            return null;
        }

        // 遍历数组元素，并强转为String类型
        List<String> newArray = new ArrayList<String>();
        for(Object item: items){
            newArray.add(item.toString());
        }
        return newArray;
    }

    @Override
    public String getDisplayString(String[] arg0) {
        return "CastArrayItem()"; // this should probably be better
    }
}
