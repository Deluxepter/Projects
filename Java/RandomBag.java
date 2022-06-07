import java.lang.Math;

public class RandomBag 
{	 
    RandomBag(int aMaxLength)
    {
        maxLength = aMaxLength;
        objects = new Object[maxLength];
        length = 0;
    }

    public boolean isEmpty() 
    {
        return length == 0;
    }

    public Object add(Object object)
    {
        if(length >= maxLength) 
        {
            return(null);
        }	
        objects[length] = object;
        length++;
        return(object);
    };

    public Object randomRemove()
    {
        if (length <= 0) 
        {
            return(null);
        }
        else 
        {
            int index = (int)(Math.random()*(length));
            Object result = objects[index];
            for (int i=index; i<(length-1); i++) 
            {
                objects[i] = objects[i+1];
            }
            objects[length-1] = null;
            length--;

            return result;
        }
    }

    public int getLength()
    {
        return (length);
    }

    private Object[] objects;
    private int length;
    private int maxLength;
}


