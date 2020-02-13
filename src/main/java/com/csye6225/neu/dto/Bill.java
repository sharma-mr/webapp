package com.csye6225.neu.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "bills")
@EntityListeners(AuditingEntityListener.class)
public class Bill {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(updatable = false, columnDefinition = "BINARY(16)")
    @JsonProperty("owner_id")
    private UUID ownerId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @CreatedDate
    @Column(name = "created_ts", nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'z'", timezone = "America/New_York")
    private Date created_ts;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "updated_ts", nullable = false)
    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'z'", timezone = "America/New_York")
    private Date updated_ts;

    @NotNull(message = "Vendor name is required")
    private String vendor;

    @JsonProperty("bill_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "America/New_York")
    @NotNull(message = "Bill date is required")
    private Date billDate;

    @JsonProperty("due_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "America/New_York")
    @NotNull(message = "Due date is required")
    private Date dueDate;


    @JsonProperty("amount_due")
    @DecimalMin(value = "0.01", message = "Minimum amount should be greater than 0")
    @NotNull(message = "Amount due is required")
    private double amountDue;

    @NotNull(message = "Categories is required")
    private String categories;

    @Enumerated(EnumType.STRING)
    @Column(name = "paymentStatus")
    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JsonProperty(value = "attachment", access = JsonProperty.Access.READ_ONLY)
    private FileAttachment fileAttachment;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public Date getCreated_ts() {
        return created_ts;
    }

    public void setCreated_ts(Date created_ts) {
        this.created_ts = created_ts;
    }

    public Date getUpdated_ts() {
        return updated_ts;
    }

    public void setUpdated_ts(Date updated_ts) {
        this.updated_ts = updated_ts;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String[] getCategories() {
        return categories.split(",");
    }

    public void setCategories(String[] input) {
        this.categories = StringUtils.arrayToCommaDelimitedString(input);
    }

    public FileAttachment getFileAttachment() {
        return fileAttachment;
    }
    public void setFileAttachment(FileAttachment fileAttachment) {
        this.fileAttachment = fileAttachment;
    }
}
