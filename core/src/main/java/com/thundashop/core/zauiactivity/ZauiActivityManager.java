package com.thundashop.core.zauiactivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.thundashop.core.pmsbookingprocess.GuestAddonsSummary;
import com.thundashop.core.pmsbookingprocess.PmsBookingProcess;
import com.thundashop.repository.utils.ZauiStatusCodes;
import com.thundashop.zauiactivity.constant.ZauiConstants;
import com.thundashop.zauiactivity.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.AccountingDetail;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductAccountingInformation;
import com.thundashop.core.productmanager.data.TaxGroup;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.repository.exceptions.NotUniqueDataException;
import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.services.octoapiservice.IOctoApiService;
import com.thundashop.services.zauiactivityservice.IZauiActivityService;

@Component
@GetShopSession
public class ZauiActivityManager extends GetShopSessionBeanNamed implements IZauiActivityManager {
    @Autowired
    IOctoApiService octoApiService;

    @Autowired
    IZauiActivityService zauiActivityService;

    @Autowired
    PmsManager pmsManager;

    @Autowired
    StoreManager storeManager;

    @Autowired
    UserManager userManager;

    @Autowired
    ProductManager productManager;

    @Autowired
    PmsBookingProcess pmsBookingProcess;

    private ZauiActivityConfig config;

    @Override
    public ZauiActivityConfig getActivityConfig() throws NotUniqueDataException {
        if (config != null) {
            return config;
        }
        config = zauiActivityService.getZauiActivityConfig(getSessionInfo());
        return config;
    }

    @Override
    public ZauiActivityConfig updateActivityConfig(ZauiActivityConfig newActivityConfig) {
        saveObject(newActivityConfig);
        config = newActivityConfig;
        return newActivityConfig;
    }

    @Override
    public List<OctoSupplier> getAllSuppliers() throws ZauiException {
        return octoApiService.getAllSuppliers();
    }

    @Override
    public List<OctoProduct> getOctoProducts(Integer supplierId) throws ZauiException {
        return octoApiService.getOctoProducts(supplierId);
    }

    @Override
    public List<OctoProductAvailability> getZauiActivityAvailability(Integer supplierId,
            OctoProductAvailabilityRequestDto availabilityRequest) throws ZauiException {
        return octoApiService.getOctoProductAvailability(supplierId, availabilityRequest);
    }

    @Override
    public OctoBooking reserveBooking(Integer supplierId, OctoBookingReserveRequest OctoBookingReserveRequest)
            throws ZauiException {
        return octoApiService.reserveBooking(supplierId, OctoBookingReserveRequest);
    }

    @Override
    public OctoBooking confirmBooking(Integer supplierId, String bookingId,
            OctoBookingConfirmRequest octoBookingConfirmRequest) throws ZauiException {
        return octoApiService.confirmBooking(supplierId, bookingId, octoBookingConfirmRequest);
    }

    @Override
    public List<ZauiActivity> getZauiActivities() throws ZauiException {
        return zauiActivityService.getZauiActivities(getSessionInfo());
    }

    @Override
    public void fetchZauiActivities() throws NotUniqueDataException {
        String currency = storeManager.getStoreSettingsApplicationKey("currencycode");
        zauiActivityService.fetchZauiActivities(getSessionInfo(), getActivityConfig(), currency);
    }

    @Override
    public void addActivityToBooking(BookingZauiActivityItem activityItem, String pmsBookingId) throws ZauiException {
        PmsBooking booking = pmsManager.getBooking(pmsBookingId);
        User booker = userManager.getUserById(booking.userId);
        booking = zauiActivityService.addActivityToBooking(activityItem, booking, booker);
        pmsManager.saveBooking(booking);
    }

    @Override
    public GuestAddonsSummary addActivityToWebBooking(AddZauiActivityToWebBookingDto activity) throws ZauiException {
        PmsBooking booking = pmsManager.getBooking(activity.getPmsBookingId());
        booking = zauiActivityService.addActivityToBooking(activity, booking, getSessionInfo());
        pmsManager.saveBooking(booking);
        return pmsBookingProcess.getAddonsSummary(new ArrayList<>());
    }

    @Override
    public GuestAddonsSummary removeActivityFromWebBooking(AddZauiActivityToWebBookingDto activity) throws ZauiException {
        PmsBooking booking = pmsManager.getBooking(activity.getPmsBookingId());
        booking = zauiActivityService.removeActivityFromWebBooking(activity, booking, getSessionInfo());
        pmsManager.saveBooking(booking);
        return pmsBookingProcess.getAddonsSummary(new ArrayList<>());
    }

    @Override
    public void cancelActivity(String pmsBookingId, String octoBookingId) throws ZauiException {
        PmsBooking booking = pmsManager.getBooking(pmsBookingId);
        BookingZauiActivityItem activityItem = booking.bookingZauiActivityItems.stream()
                .filter(item -> item.getOctoBooking().getId().equals(octoBookingId))
                .findFirst()
                .orElse(null);
        zauiActivityService.cancelActivityFromBooking(activityItem);
        pmsManager.saveBooking(booking);
    }

    @Override
    public List<CartItem> getZauiActivityCartItems(String productId, String addonId) throws ErrorException {
        Optional<ZauiActivity> activity = zauiActivityService.getZauiActivityById(productId, getSessionInfo());
        Optional<BookingZauiActivityItem> activityItem = zauiActivityService
                .getBookingZauiActivityItemByAddonId(addonId, pmsManager.getSessionInfo());
        if (!activityItem.isPresent()) {
            throw new ErrorException(1011);
        }
        List<CartItem> cartItems = new ArrayList<>();
        Pricing pricing = activityItem.get().getOctoBooking().getPricing();
        pricing.getIncludedTaxes().forEach((tax) -> {
            Product taxProduct = null;
            try {
                taxProduct = createZauiActivityForTax(activity.get(), tax, pricing.getCurrencyPrecision());
            } catch (ZauiException | NotUniqueDataException e) {
                throw new RuntimeException(e);
            }
            CartItem cartItem = new CartItem();
            cartItem.setProduct(taxProduct);
            cartItem.setCount(1);
            cartItems.add(cartItem);
        });
        return cartItems;
    }

    private Product createZauiActivityForTax(ZauiActivity product, TaxData activity, Integer currencyPrecision) throws ZauiException, NotUniqueDataException {
        List<TaxGroup> taxes = productManager.getTaxes();
        Product taxProduct = product.clone();
        AccountingDetail account = getOctoSupplierAccount(product.getSupplierId(), activity.getRate().doubleValue());

        ProductAccountingInformation info = new ProductAccountingInformation();
        info.accountingNumber = account.accountNumber + "";
        info.taxGroupNumber = account.getShopTaxGroup;

        TaxGroup taxGroup = taxes.get(account.getShopTaxGroup);
        taxProduct.accountingConfig.add(info);
        taxProduct.taxgroup = account.getShopTaxGroup;
        taxProduct.taxGroupObject = taxGroup;
        taxProduct.name = activity.getName();
        taxProduct.metaData = product.name;
        taxProduct.masterProductId = product.id;
        taxProduct.priceExTaxes = zauiActivityService.getPrecisedPrice(activity.getPriceExcludingTax(),
                currencyPrecision);
        taxProduct.price = zauiActivityService.getPrecisedPrice(activity.getTaxAmount(), currencyPrecision)
                + taxProduct.priceExTaxes;
        return taxProduct;
    }

    private AccountingDetail getOctoSupplierAccount(Integer supplierId, Double taxRate) throws ZauiException, NotUniqueDataException {
        ZauiConnectedSupplier zauiSupplier = getActivityConfig().connectedSuppliers.stream().filter(supplier -> supplierId.equals(supplier.getId())).findFirst().orElse(null);
        if(zauiSupplier == null)
            throw new ZauiException(ZauiStatusCodes.SUPPLIER_NOT_FOUND);
        int accountNumber = Integer.parseInt(zauiSupplier.getSupplierAccountNumberByRate(taxRate));
        return productManager.getAccountingDetail(accountNumber);
    }
}
