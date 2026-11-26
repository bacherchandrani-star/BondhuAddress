
package com.bondhubazar.address.bondhuaddress;

// List এবং ArrayList ব্যবহার করার জন্য
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

// App Inventor প্রয়োজনীয় ক্লাস
import com.google.appinventor.components.annotations.Extension;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.util.YailList;

@Extension(
    description = "Bondhu Bazar Delivery Address Manager"
)

public class BondhuAddress extends AndroidNonvisibleComponent {

    // সব Address রাখার জন্য LinkedHashMap
    private LinkedHashMap<String, AddressData> addresses =
        new LinkedHashMap<>();

    // Address ID তৈরি করার জন্য Counter
    private int addressCounter = 1;

    // Default Address ID
    private String defaultAddressId = "";

    // Address-এর তথ্য রাখার Class
    private static class AddressData {

        String id;
        String name;
        String phone;
        String house;
        String area;
        String city;
        String state;
        String pin;

        // Constructor
        AddressData(
            String id,
            String name,
            String phone,
            String house,
            String area,
            String city,
            String state,
            String pin
        ) {

            this.id = id;
            this.name = name;
            this.phone = phone;
            this.house = house;
            this.area = area;
            this.city = city;
            this.state = state;
            this.pin = pin;
        }
    }

    // Constructor
    public BondhuAddress(ComponentContainer container) {
        super(container.$form());
    }

    // ==================================
    // ১. নতুন Address যোগ করা
    // ==================================

    @SimpleFunction(
        description = "Add a new delivery address"
    )
    public String AddAddress(
        String name,
        String phone,
        String house,
        String area,
        String city,
        String state,
        String pin
    ) {

        // নতুন Unique ID তৈরি
        String id = "ADDR_" + addressCounter++;

        // নতুন Address তৈরি
        AddressData address = new AddressData(
            id,
            name,
            phone,
            house,
            area,
            city,
            state,
            pin
        );

        // Map-এ Address রাখা
        addresses.put(id, address);

        // প্রথম Address-কে Default করা
        if (defaultAddressId.isEmpty()) {
            defaultAddressId = id;
        }

        // Event পাঠানো
        AddressAdded(id);

        // ID ফেরত দেওয়া
        return id;
    }

    // ==================================
    // ২. Address Update করা
    // ==================================

    @SimpleFunction(
        description = "Update existing address"
    )
    public boolean UpdateAddress(
        String id,
        String name,
        String phone,
        String house,
        String area,
        String city,
        String state,
        String pin
    ) {

        // Address খোঁজা
        AddressData address = addresses.get(id);

        // Address না থাকলে
        if (address == null) {
            return false;
        }

        // নতুন তথ্য বসানো
        address.name = name;
        address.phone = phone;
        address.house = house;
        address.area = area;
        address.city = city;
        address.state = state;
        address.pin = pin;

        AddressUpdated(id);

        return true;
    }

    // ==================================
    // ৩. Address Delete করা
    // ==================================

    @SimpleFunction(
        description = "Delete an address"
    )
    public boolean DeleteAddress(String id) {

        // Address আছে কি না পরীক্ষা
        if (!addresses.containsKey(id)) {
            return false;
        }

        // Address মুছে ফেলা
        addresses.remove(id);

        // Default Address মুছে গেলে
        if (defaultAddressId.equals(id)) {

            defaultAddressId = "";

            // অন্য Address থাকলে প্রথমটিকে Default করা
            if (!addresses.isEmpty()) {
                defaultAddressId =
                    addresses.keySet().iterator().next();
            }
        }

        AddressDeleted(id);

        return true;
    }

    // ==================================
    // ৪. Default Address নির্বাচন
    // ==================================

    @SimpleFunction(
        description = "Set default delivery address"
    )
    public boolean SetDefaultAddress(String id) {

        if (!addresses.containsKey(id)) {
            return false;
        }

        defaultAddressId = id;

        DefaultAddressChanged(id);

        return true;
    }

    // ==================================
    // ৫. Default Address ID পাওয়া
    // ==================================

    @SimpleFunction(
        description = "Get default address ID"
    )
    public String GetDefaultAddressId() {

        return defaultAddressId;
    }

    // ==================================
    // ৬. সব Address ID পাওয়া
    // ==================================

    @SimpleFunction(
        description = "Get all address IDs"
    )
    public YailList GetAllAddressIds() {

        ArrayList<String> ids = new ArrayList<>();

        ids.addAll(addresses.keySet());

        return YailList.makeList(ids);
    }

    // ==================================
    // ৭. Address-এর সম্পূর্ণ তথ্য পাওয়া
    // ==================================

    @SimpleFunction(
        description = "Get address information as a list"
    )
    public YailList GetAddress(String id) {

        AddressData address = addresses.get(id);

        ArrayList<Object> data = new ArrayList<>();

        if (address == null) {
            return YailList.makeEmptyList();
        }

        data.add(address.id);
        data.add(address.name);
        data.add(address.phone);
        data.add(address.house);
        data.add(address.area);
        data.add(address.city);
        data.add(address.state);
        data.add(address.pin);

        data.add(defaultAddressId.equals(id));

        return YailList.makeList(data);
    }

    // ==================================
    // ৮. মোট Address সংখ্যা
    // ==================================

    @SimpleFunction(
        description = "Get total number of addresses"
    )
    public int GetAddressCount() {

        return addresses.size();
    }

    // ==================================
    // ৯. Address আছে কি না পরীক্ষা
    // ==================================

    @SimpleFunction(
        description = "Check whether address exists"
    )
    public boolean HasAddress(String id) {

        return addresses.containsKey(id);
    }

    // ==================================
    // ১০. সব Address মুছে ফেলা
    // ==================================

    @SimpleFunction(
        description = "Delete all addresses"
    )
    public void ClearAddresses() {

        addresses.clear();

        defaultAddressId = "";

        AddressListCleared();
    }

    // ==================================
    // ১১. Address যোগ করার Event
    // ==================================

    @SimpleEvent(
        description = "Triggered when address is added"
    )
    public void AddressAdded(String id) {

        EventDispatcher.dispatchEvent(
            this,
            "AddressAdded",
            id
        );
    }

    // ==================================
    // ১২. Address Update Event
    // ==================================

    @SimpleEvent(
        description = "Triggered when address is updated"
    )
    public void AddressUpdated(String id) {

        EventDispatcher.dispatchEvent(
            this,
            "AddressUpdated",
            id
        );
    }

    // ==================================
    // ১৩. Address Delete Event
    // ==================================

    @SimpleEvent(
        description = "Triggered when address is deleted"
    )
    public void AddressDeleted(String id) {

        EventDispatcher.dispatchEvent(
            this,
            "AddressDeleted",
            id
        );
    }

    // ==================================
    // ১৪. Default Address পরিবর্তন Event
    // ==================================

    @SimpleEvent(
        description = "Triggered when default address changes"
    )
    public void DefaultAddressChanged(String id) {

        EventDispatcher.dispatchEvent(
            this,
            "DefaultAddressChanged",
            id
        );
    }

    // ==================================
    // ১৫. সব Address Clear Event
    // ==================================

    @SimpleEvent(
        description = "Triggered when all addresses are cleared"
    )
    public void AddressListCleared() {

        EventDispatcher.dispatchEvent(
            this,
            "AddressListCleared"
        );
    }
}
