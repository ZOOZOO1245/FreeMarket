const mongoose = require('mongoose');

const OrderSchema = new mongoose.Schema({
    merchant_uid: {
        type: String,
        required: true,
        unique: true
    },
    imp_uid: String,
    user_id: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    items: [{
        product_id: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'Product',
            required: true
        },
        quantity: {
            type: Number,
            required: true,
            min: 1
        },
        price: {
            type: Number,
            required: true
        }
    }],
    total_amount: {
        type: Number,
        required: true
    },
    shipping_fee: {
        type: Number,
        required: true
    },
    payment_method: {
        type: String,
        required: true,
        enum: ['kakaopay', 'naverpay', 'samsungpay', 'card']
    },
    payment_status: {
        type: String,
        required: true,
        enum: ['pending', 'paid', 'failed', 'cancelled'],
        default: 'pending'
    },
    shipping_status: {
        type: String,
        required: true,
        enum: ['preparing', 'shipping', 'delivered'],
        default: 'preparing'
    },
    shipping_address: {
        recipient: String,
        phone: String,
        address: String,
        detail_address: String,
        postal_code: String
    },
    created_at: {
        type: Date,
        default: Date.now
    },
    updated_at: {
        type: Date,
        default: Date.now
    }
});

module.exports = mongoose.model('Order', OrderSchema); 