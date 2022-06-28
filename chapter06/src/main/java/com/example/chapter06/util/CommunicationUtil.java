package com.example.chapter06.util;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;

import com.example.chapter06.bean.Contact;
import com.example.chapter06.bean.SmsContent;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("DefaultLocale")
public class CommunicationUtil {
    private final static String TAG = "CommunicationUtil";
    private static Uri mContactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    private static String[] mContactColumn = new String[]{
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

    // 读取手机保存的联系人数量
    public static int readPhoneContacts(ContentResolver resolver) {
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        Cursor cursor = resolver.query(mContactUri, mContactColumn, null, null, null);
        while (cursor.moveToNext()) {
            Contact contact = new Contact();
            contact.phone = cursor.getString(0).replace("+86", "").replace(" ", "");
            contact.name = cursor.getString(1);
            Log.d(TAG, contact.name + " " + contact.phone);
            contactList.add(contact);
        }
        cursor.close();
        return contactList.size();
    }

    // 读取sim卡保存的联系人数量
    public static int readSimContacts(ContentResolver resolver) {
        Uri simUri = Uri.parse("content://icc/adn");
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        Cursor cursor = resolver.query(simUri, mContactColumn, null, null, null);
        while (cursor.moveToNext()) {
            Contact contact = new Contact();
            contact.phone = cursor.getString(0).replace("+86", "").replace(" ", "");
            contact.name = cursor.getString(1);
            Log.d(TAG, contact.name + " " + contact.phone);
            contactList.add(contact);
        }
        cursor.close();
        return contactList.size();
    }

    // 往手机通讯录添加一个联系人信息（包括姓名、电话号码、电子邮箱）
    public static void addContacts(ContentResolver resolver, Contact contact) {
        // 构建一个指向系统联系人提供器的Uri对象
        Uri raw_uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentValues values = new ContentValues(); // 创建新的配对
        // 往 raw_contacts 添加联系人记录，并获取添加后的联系人编号
        long contactId = ContentUris.parseId(resolver.insert(raw_uri, values));
        // 构建一个指向系统联系人数据的Uri对象
        Uri uri = Uri.parse("content://com.android.contacts/data");
        ContentValues name = new ContentValues(); // 创建新的配对
        name.put("raw_contact_id", contactId); // 往配对添加联系人编号
        // 往配对添加“姓名”的数据类型
        name.put("mimetype", "vnd.android.cursor.item/name");
        name.put("data2", contact.name); // 往配对添加联系人的姓名
        resolver.insert(uri, name); // 往提供器添加联系人的姓名记录
        ContentValues phone = new ContentValues(); // 创建新的配对
        phone.put("raw_contact_id", contactId); // 往配对添加联系人编号
        // 往配对添加“电话号码”的数据类型
        phone.put("mimetype", "vnd.android.cursor.item/phone_v2");
        phone.put("data1", contact.phone); // 往配对添加联系人的电话号码
        phone.put("data2", "2"); // 联系类型。1表示家庭，2表示工作
        resolver.insert(uri, phone); // 往提供器添加联系人的号码记录
        ContentValues email = new ContentValues(); // 创建新的配对
        email.put("raw_contact_id", contactId); // 往配对添加联系人编号
        // 往配对添加“电子邮箱”的数据类型
        email.put("mimetype", "vnd.android.cursor.item/email_v2");
        email.put("data1", contact.email); // 往配对添加联系人的电子邮箱
        email.put("data2", "2"); // 联系类型。1表示家庭，2表示工作
        resolver.insert(uri, email); // 往提供器添加联系人的邮箱记录
    }

    // 往手机通讯录一次性添加一个联系人信息（包括主记录、姓名、电话号码、电子邮箱）
    public static void addFullContacts(ContentResolver resolver, Contact contact) {
        // 构建一个指向系统联系人提供器的Uri对象
        Uri raw_uri = Uri.parse("content://com.android.contacts/raw_contacts");
        // 构建一个指向系统联系人数据的Uri对象
        Uri uri = Uri.parse("content://com.android.contacts/data");
        // 创建一个插入联系人主记录的内容操作器
        ContentProviderOperation op_main = ContentProviderOperation
                .newInsert(raw_uri).withValue("account_name", null).build();
        // 创建一个插入联系人姓名记录的内容操作器
        ContentProviderOperation op_name = ContentProviderOperation
                .newInsert(uri).withValueBackReference("raw_contact_id", 0)
                .withValue("mimetype", "vnd.android.cursor.item/name")
                .withValue("data2", contact.name).build();
        // 创建一个插入联系人电话号码记录的内容操作器
        ContentProviderOperation op_phone = ContentProviderOperation
                .newInsert(uri).withValueBackReference("raw_contact_id", 0)
                .withValue("mimetype", "vnd.android.cursor.item/phone_v2")
                .withValue("data2", "2").withValue("data1", contact.phone).build();
        // 创建一个插入联系人电子邮箱记录的内容操作器
        ContentProviderOperation op_email = ContentProviderOperation
                .newInsert(uri).withValueBackReference("raw_contact_id", 0)
                .withValue("mimetype", "vnd.android.cursor.item/email_v2")
                .withValue("data2", "2").withValue("data1", contact.email).build();
        // 声明一个内容操作器的列表，并将上面四个操作器添加到该列表中
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        operations.add(op_main);
        operations.add(op_name);
        operations.add(op_phone);
        operations.add(op_email);
        try {
            // 批量提交四个内容操作器所做的修改
            resolver.applyBatch("com.android.contacts", operations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //private static Uri mSmsUri = Uri.parse("content://sms"); //该地址表示所有短信，包括收件箱和发件箱
    //private static Uri mSmsUri = Uri.parse("content://sms/inbox"); //该地址为收件箱

    private static Uri mSmsUri;
    private static String[] mSmsColumn;

    // 读取指定号码发来的短信记录
    public static int readSms(ContentResolver resolver, String phone, int gaps) {
        mSmsUri = Telephony.Sms.Inbox.CONTENT_URI;
        mSmsColumn = new String[]{
                Telephony.Sms.ADDRESS, Telephony.Sms.PERSON,
                Telephony.Sms.BODY, Telephony.Sms.DATE,
                Telephony.Sms.TYPE};
        ArrayList<SmsContent> smsList = new ArrayList<SmsContent>();
        String selection = "";
        if (phone != null && phone.length() > 0) {
            selection = String.format("address='%s'", phone);
        }
        if (gaps > 0) {
            selection = String.format("%s%sdate>%d", selection,
                    (selection.length() > 0) ? " and " : "", System.currentTimeMillis() - gaps * 1000);
        }
        Cursor cursor = resolver.query(mSmsUri, mSmsColumn, selection, null, "date desc");
        while (cursor.moveToNext()) {
            SmsContent sms = new SmsContent();
            sms.address = cursor.getString(0);
            sms.person = cursor.getString(1);
            sms.body = cursor.getString(2);
            sms.date = DateUtil.formatDate(cursor.getLong(3));
            sms.type = cursor.getInt(4);  //type=1表示收到的短信，type=2表示发送的短信
            Log.d(TAG, sms.address + " " + sms.person + " " + sms.date + " " + sms.type + " " + sms.body);
            smsList.add(sms);
        }
        cursor.close();
        return smsList.size();
    }

    private static Uri mRecordUri = CallLog.Calls.CONTENT_URI;
    private static String[] mRecordColumn = new String[]{
            CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.TYPE,
            CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.NEW};

    // 读取所有的联系人信息
    public static List<Contact> readAllContacts(ContentResolver resolver) {
        List<Contact> contactList = new ArrayList<Contact>();
        // 查询结果按照联系人id降序排列，也就是越新的记录会排在越前面
        Cursor cursor = resolver.query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, "_id desc");
        int contactIdIndex = 0;
        int nameIndex = 0;

        if (cursor.getCount() > 0) {
            contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        }
        while (cursor.moveToNext()) {
            Contact contact = new Contact();
            contact.contactId = cursor.getString(contactIdIndex);
            contact.name = cursor.getString(nameIndex);
            contactList.add(contact);
        }
        cursor.close();

        for (int i = 0; i < contactList.size(); i++) {
            Contact contact = contactList.get(i);
            contact.phone = getColumn(resolver, contact.contactId,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.NUMBER);
            contact.email = getColumn(resolver, contact.contactId,
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Email.DATA);
            contactList.set(i, contact);
            Log.d(TAG, contact.contactId + " " + contact.name + " " + contact.phone + " " + contact.email);
        }
        return contactList;
    }

    private static String getColumn(ContentResolver resolver, String contactId,
                                    Uri uri, String selection, String column) {
        Cursor cursor = resolver.query(uri, null, selection + "=" + contactId, null, null);
        int index = 0;
        if (cursor.getCount() > 0) {
            index = cursor.getColumnIndex(column);
        }
        String value = "";
        while (cursor.moveToNext()) {
            value = cursor.getString(index);
        }
        cursor.close();
        return value;
    }

}
