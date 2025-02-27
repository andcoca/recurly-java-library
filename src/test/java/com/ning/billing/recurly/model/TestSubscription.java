/*
 * Copyright 2010-2014 Ning, Inc.
 * Copyright 2014-2015 The Billing Project, LLC
 *
 * The Billing Project licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.ning.billing.recurly.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import com.ning.billing.recurly.TestUtils;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

public class TestSubscription extends TestModelBase {

    @Test(groups = "fast")
    public void testDeserialization() throws Exception {
        // See https://dev.recurly.com/docs/list-subscriptions
        final String subscriptionData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                        "<subscription href=\"https://api.recurly.com/v2/subscriptions/44f83d7cba354d5b84812419f923ea96\">\n" +
                                        "  <account href=\"https://api.recurly.com/v2/accounts/1\"/>\n" +
                                        "  <plan href=\"https://api.recurly.com/v2/plans/gold\">\n" +
                                        "    <plan_code>gold</plan_code>\n" +
                                        "    <name>Gold plan</name>\n" +
                                        "  </plan>\n" +
                                        "  <uuid>44f83d7cba354d5b84812419f923ea96</uuid>\n" +
                                        "  <state>active</state>\n" +
                                        "  <unit_amount_in_cents type=\"integer\">800</unit_amount_in_cents>\n" +
                                        "  <currency>EUR</currency>\n" +
                                        "  <quantity type=\"integer\">1</quantity>\n" +
                                        "  <activated_at type=\"dateTime\">2011-05-27T07:00:00Z</activated_at>\n" +
                                        "  <updated_at type=\"dateTime\">2011-05-27T07:00:00Z</updated_at>\n" +
                                        "  <canceled_at nil=\"nil\"></canceled_at>\n" +
                                        "  <expires_at nil=\"nil\"></expires_at>\n" +
                                        "  <current_period_started_at type=\"dateTime\">2011-06-27T07:00:00Z</current_period_started_at>\n" +
                                        "  <current_period_ends_at type=\"dateTime\">2010-07-27T07:00:00Z</current_period_ends_at>\n" +
                                        "  <trial_started_at nil=\"nil\"></trial_started_at>\n" +
                                        "  <trial_ends_at nil=\"nil\"></trial_ends_at>\n" +
                                        "  <starts_at>2010-07-28T07:00:00Z</starts_at>\n" +
                                        "  <a name=\"cancel\" href=\"https://api.recurly.com/v2/subscriptions/44f83d7cba354d5b84812419f923ea96/cancel\" method=\"put\"/>\n" +
                                        "  <a name=\"terminate\" href=\"https://api.recurly.com/v2/subscriptions/44f83d7cba354d5b84812419f923ea96/terminate\" method=\"put\"/>\n" +
                                        "  <a name=\"postpone\" href=\"https://api.recurly.com/v2/subscriptions/44f83d7cba354d5b84812419f923ea96/postpone\" method=\"put\"/>\n" +
                                        "  <collection_method>manual</collection_method>\n" +
                                        "  <net_terms type=\"integer\">10</net_terms>\n" +
                                        "  <po_number>PO19384</po_number>\n" +
                                        "  <tax_in_cents type=\"integer\">394</tax_in_cents>\n" +
                                        "  <tax_type>usst</tax_type>\n" +
                                        "  <tax_region>CA</tax_region>\n" +
                                        "  <tax_rate type=\"float\">0.0875</tax_rate>\n" +
                                        "  <revenue_schedule_type>evenly</revenue_schedule_type>\n" +
                                        "  <first_renewal_date type=\"dateTime\">2011-07-01T07:00:00Z</first_renewal_date>\n" +
                                        "  <started_with_gift type=\"boolean\">true</started_with_gift>\n" +
                                        "  <converted_at type=\"dateTime\">2017-06-27T00:00:00Z</converted_at>" +
                                        "  <no_billing_info_reason>plan_free_trial</no_billing_info_reason>" +
                                        "  <imported_trial type=\"boolean\">true</imported_trial>" +
                                        "  <subscription_add_ons type=\"array\">\n" +
                                        "  </subscription_add_ons>\n" +
                                        "  <coupon_codes type=\"array\">\n" +
                                        "    <coupon_code>123</coupon_code>\n" +
                                        "    <coupon_code>abc</coupon_code>\n" +
                                        "  </coupon_codes>\n" +
                                        "  <pending_subscription type=\"subscription\">\n" +
                                        "    <plan href=\"https://api.recurly.com/v2/plans/silver\">\n" +
                                        "      <plan_code>silver</plan_code>\n" +
                                        "      <name>Silver plan</name>\n" +
                                        "    </plan>\n" +
                                        "    <unit_amount_in_cents type=\"integer\">400</unit_amount_in_cents>\n" +
                                        "    <quantity type=\"integer\">1</quantity>\n" +
                                        "    <subscription_add_ons type=\"array\">\n" +
                                        "    </subscription_add_ons>\n" +
                                        "  </pending_subscription>\n" +
                                        "</subscription>";

        final Subscription subscription = verifySubscription(subscriptionData);
        verifyPaginationData(subscription);
        verifyPendingSubscription(subscription);
        Assert.assertEquals(subscription.getAddOns().size(), 0);

        ArrayList<String> codes = new ArrayList<String>();
        codes.add("123");
        codes.add("abc");
        assertEquals(subscription.getCouponCodes(), codes);
    }

    @Test(groups = "fast")
    public void testDeserializationWithAddons() throws Exception {
        // See https://dev.recurly.com/docs/subscription-add-ons
        final String subscriptionData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                        "<subscription href=\"https://api.recurly.com/v2/subscriptions/44f83d7cba354d5b84812419f923ea96\">\n" +
                                        "  <account href=\"https://api.recurly.com/v2/accounts/1\"/>\n" +
                                        "  <plan href=\"https://api.recurly.com/v2/plans/gold\">\n" +
                                        "    <plan_code>gold</plan_code>\n" +
                                        "    <name>Gold plan</name>\n" +
                                        "  </plan>\n" +
                                        "  <uuid>44f83d7cba354d5b84812419f923ea96</uuid>\n" +
                                        "  <state>active</state>\n" +
                                        "  <unit_amount_in_cents type=\"integer\">800</unit_amount_in_cents>\n" +
                                        "  <currency>EUR</currency>\n" +
                                        "  <quantity type=\"integer\">1</quantity>\n" +
                                        "  <activated_at type=\"dateTime\">2011-05-27T07:00:00Z</activated_at>\n" +
                                        "  <updated_at type=\"dateTime\">2011-05-27T07:00:00Z</updated_at>\n" +
                                        "  <canceled_at nil=\"nil\"></canceled_at>\n" +
                                        "  <expires_at nil=\"nil\"></expires_at>\n" +
                                        "  <current_period_started_at type=\"dateTime\">2011-06-27T07:00:00Z</current_period_started_at>\n" +
                                        "  <current_period_ends_at type=\"dateTime\">2010-07-27T07:00:00Z</current_period_ends_at>\n" +
                                        "  <trial_started_at nil=\"nil\"></trial_started_at>\n" +
                                        "  <trial_ends_at nil=\"nil\"></trial_ends_at>\n" +
                                        "  <starts_at>2010-07-28T07:00:00Z</starts_at>\n" +
                                        "  <a name=\"cancel\" href=\"https://api.recurly.com/v2/subscriptions/44f83d7cba354d5b84812419f923ea96/cancel\" method=\"put\"/>\n" +
                                        "  <a name=\"terminate\" href=\"https://api.recurly.com/v2/subscriptions/44f83d7cba354d5b84812419f923ea96/terminate\" method=\"put\"/>\n" +
                                        "  <a name=\"postpone\" href=\"https://api.recurly.com/v2/subscriptions/44f83d7cba354d5b84812419f923ea96/postpone\" method=\"put\"/>\n" +
                                        "  <collection_method>manual</collection_method>\n" +
                                        "  <net_terms type=\"integer\">10</net_terms>\n" +
                                        "  <po_number>PO19384</po_number>\n" +
                                        "  <tax_in_cents type=\"integer\">394</tax_in_cents>\n" +
                                        "  <tax_type>usst</tax_type>\n" +
                                        "  <tax_region>CA</tax_region>\n" +
                                        "  <tax_rate type=\"float\">0.0875</tax_rate>\n" +
                                        "  <first_renewal_date type=\"dateTime\">2011-07-01T07:00:00Z</first_renewal_date>\n" +
                                        "  <revenue_schedule_type>evenly</revenue_schedule_type>\n" +
                                        "  <started_with_gift type=\"boolean\">true</started_with_gift>\n" +
                                        "  <converted_at type=\"dateTime\">2017-06-27T00:00:00Z</converted_at>" +
                                        "  <no_billing_info_reason>plan_free_trial</no_billing_info_reason>" +
                                        "  <imported_trial type=\"boolean\">true</imported_trial>" +
                                        "  <subscription_add_ons type=\"array\">\n" +
                                        "    <subscription_add_on>\n" +
                                        "      <add_on_code>extra_users</add_on_code>\n" +
                                        "      <quantity>2</quantity>\n" +
                                        "      <unit_amount_in_cents>1000</unit_amount_in_cents>\n" +
                                        "      <usage_percentage type=\"float\">2.1</usage_percentage>\n" +
                                        "      <revenue_schedule_type>evenly</revenue_schedule_type>\n" +
                                        "    </subscription_add_on>\n" +
                                        "    <subscription_add_on>\n" +
                                        "      <add_on_code>extra_ip</add_on_code>\n" +
                                        "      <quantity>3</quantity>\n" +
                                        "      <unit_amount_in_cents>200</unit_amount_in_cents>\n" +
                                        "    </subscription_add_on>\n" +
                                        "    <subscription_add_on>\n" +
                                        "      <add_on_code>mockitem</add_on_code>\n" +
                                        "      <quantity>1</quantity>\n" +
                                        "      <unit_amount_in_cents>199</unit_amount_in_cents>\n" +
                                        "      <add_on_source>item</add_on_source>\n" +
                                        "    </subscription_add_on>\n" +
                                        "   </subscription_add_ons>" +
                                        "</subscription>";

        final Subscription subscription = verifySubscription(subscriptionData);
        verifySubscriptionAddons(subscription);
        verifyPaginationData(subscription);

        // Verify we can serialize them properly
        final String subscriptionDataSerialized = xmlMapper.writeValueAsString(subscription);
        final Subscription subscription2 = verifySubscription(subscriptionDataSerialized);
        verifySubscriptionAddons(subscription2);
    }

    public void testDeserializationWithCustomFields() throws Exception {
        // See https://dev.recurly.com/docs/custom-fields
        final String subscriptionData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<subscription href=\"https://api.recurly.com/v2/subscriptions/44f83d7cba354d5b84812419f923ea96\">\n" +
                "  <account href=\"https://api.recurly.com/v2/accounts/1\"/>\n" +
                "  <plan href=\"https://api.recurly.com/v2/plans/gold\">\n" +
                "    <plan_code>gold</plan_code>\n" +
                "    <name>Gold plan</name>\n" +
                "  </plan>\n" +
                "  <uuid>44f83d7cba354d5b84812419f923ea96</uuid>\n" +
                "  <state>active</state>\n" +
                "  <unit_amount_in_cents type=\"integer\">800</unit_amount_in_cents>\n" +
                "  <currency>EUR</currency>\n" +
                "  <quantity type=\"integer\">1</quantity>\n" +
                "  <activated_at type=\"dateTime\">2011-05-27T07:00:00Z</activated_at>\n" +
                "  <updated_at type=\"dateTime\">2011-05-27T07:00:00Z</updated_at>\n" +
                "  <canceled_at nil=\"nil\"></canceled_at>\n" +
                "  <expires_at nil=\"nil\"></expires_at>\n" +
                "  <current_period_started_at type=\"dateTime\">2011-06-27T07:00:00Z</current_period_started_at>\n" +
                "  <current_period_ends_at type=\"dateTime\">2010-07-27T07:00:00Z</current_period_ends_at>\n" +
                "  <trial_started_at nil=\"nil\"></trial_started_at>\n" +
                "  <trial_ends_at nil=\"nil\"></trial_ends_at>\n" +
                "  <starts_at>2010-07-28T07:00:00Z</starts_at>\n" +
                "  <a name=\"cancel\" href=\"https://api.recurly.com/v2/subscriptions/44f83d7cba354d5b84812419f923ea96/cancel\" method=\"put\"/>\n" +
                "  <a name=\"terminate\" href=\"https://api.recurly.com/v2/subscriptions/44f83d7cba354d5b84812419f923ea96/terminate\" method=\"put\"/>\n" +
                "  <a name=\"postpone\" href=\"https://api.recurly.com/v2/subscriptions/44f83d7cba354d5b84812419f923ea96/postpone\" method=\"put\"/>\n" +
                "  <collection_method>manual</collection_method>\n" +
                "  <net_terms type=\"integer\">10</net_terms>\n" +
                "  <po_number>PO19384</po_number>\n" +
                "  <tax_in_cents type=\"integer\">394</tax_in_cents>\n" +
                "  <tax_type>usst</tax_type>\n" +
                "  <tax_region>CA</tax_region>\n" +
                "  <tax_rate type=\"float\">0.0875</tax_rate>\n" +
                "  <first_renewal_date type=\"dateTime\">2011-07-01T07:00:00Z</first_renewal_date>\n" +
                "  <revenue_schedule_type>evenly</revenue_schedule_type>\n" +
                "  <started_with_gift type=\"boolean\">true</started_with_gift>\n" +
                "  <converted_at type=\"dateTime\">2017-06-27T00:00:00Z</converted_at>" +
                "  <no_billing_info_reason>plan_free_trial</no_billing_info_reason>" +
                "  <imported_trial type=\"boolean\">true</imported_trial>" +
                "  <custom_fields type=\"array\">\n" +
                "    <custom_field>\n" +
                "      <name>field1</add_on_code>\n" +
                "      <value>one value</quantity>\n" +
                "    </custom_field>\n" +
                "    <custom_field>\n" +
                "      <name>field2</name>\n" +
                "      <value>second value</value>\n" +
                "    </custom_field>\n" +
                "   </custom_fields>" +
                "</subscription>";

        final Subscription subscription = verifySubscription(subscriptionData);
        verifySubscriptionCustomFields(subscription);
        verifyPaginationData(subscription);

        // Verify we can serialize them properly
        final String subscriptionDataSerialized = xmlMapper.writeValueAsString(subscription);
        final Subscription subscription2 = verifySubscription(subscriptionDataSerialized);
        verifySubscriptionCustomFields(subscription2);
    }

    @Test(groups = "fast")
    public void testDeserializationWithRampIntervals() throws Exception {
        // See https://dev.recurly.com/docs/list-subscriptions
        final String subscriptionData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                        "<subscription href=\"https://api.recurly.com/v2/subscriptions/44f83d7cba354d5b84812419f923ea96\">\n" +
                                        "  <account href=\"https://api.recurly.com/v2/accounts/1\"/>\n" +
                                        "  <plan href=\"https://api.recurly.com/v2/plans/gold\">\n" +
                                        "    <plan_code>gold</plan_code>\n" +
                                        "    <name>Gold plan</name>\n" +
                                        "  </plan>\n" +
                                        "  <uuid>44f83d7cba354d5b84812419f923ea96</uuid>\n" +
                                        "  <state>active</state>\n" +
                                        "  <ramp_intervals type=\"array\">\n" +
                                        "    <ramp_interval>\n" +
                                        "      <starting_billing_cycle type=\"integer\">1</starting_billing_cycle>\n" +
                                        "      <unit_amount_in_cents type=\"integer\">2000</unit_amount_in_cents>\n" +
                                        "      <remaining_billing_cycles type=\"integer\">0</remaining_billing_cycles>\n" +
                                        "      <starting_on type=\"dateTime\">2011-06-27T07:00:00Z</starting_on>\n" +
                                        "      <ending_on type=\"dateTime\">2011-07-27T07:00:00Z</ending_on>\n" +
                                        "    </ramp_interval>\n" +
                                        "    <ramp_interval>\n" +
                                        "      <starting_billing_cycle type=\"integer\">2</starting_billing_cycle>\n" +
                                        "      <unit_amount_in_cents type=\"integer\">4000</unit_amount_in_cents>\n" +
                                        "      <remaining_billing_cycles type=\"integer\">5</remaining_billing_cycles>\n" +
                                        "      <starting_on type=\"dateTime\">2011-07-27T07:00:00Z</starting_on>\n" +
                                        "      <ending_on type=\"dateTime\">2011-08-27T07:00:00Z</ending_on>\n" +
                                        "    </ramp_interval>\n" +
                                        "    <ramp_interval>\n" +
                                        "      <starting_billing_cycle type=\"integer\">7</starting_billing_cycle>\n" +
                                        "      <unit_amount_in_cents type=\"integer\">7000</unit_amount_in_cents>\n" +
                                        "      <remaining_billing_cycles nil=\"nil\"/>\n" +
                                        "      <starting_on type=\"dateTime\">2011-08-27T07:00:00Z</starting_on>\n" +
                                        "      <ending_on nil=\"nil\"></ending_on>\n" +
                                        "    </ramp_interval>\n" +
                                        "  </ramp_intervals>\n" +
                                        "  <unit_amount_in_cents type=\"integer\">2000</unit_amount_in_cents>\n" +
                                        "  <currency>EUR</currency>\n" +
                                        "  <quantity type=\"integer\">1</quantity>\n" +
                                        "  <activated_at type=\"dateTime\">2011-05-27T07:00:00Z</activated_at>\n" +
                                        "  <updated_at type=\"dateTime\">2011-05-27T07:00:00Z</updated_at>\n" +
                                        "  <canceled_at nil=\"nil\"></canceled_at>\n" +
                                        "  <expires_at nil=\"nil\"></expires_at>\n" +
                                        "  <current_period_started_at type=\"dateTime\">2011-06-27T07:00:00Z</current_period_started_at>\n" +
                                        "  <current_period_ends_at type=\"dateTime\">2010-07-27T07:00:00Z</current_period_ends_at>\n" +
                                        "  <trial_started_at nil=\"nil\"></trial_started_at>\n" +
                                        "  <trial_ends_at nil=\"nil\"></trial_ends_at>\n" +
                                        "  <starts_at>2010-07-28T07:00:00Z</starts_at>\n" +
                                        "  <a name=\"cancel\" href=\"https://api.recurly.com/v2/subscriptions/44f83d7cba354d5b84812419f923ea96/cancel\" method=\"put\"/>\n" +
                                        "  <a name=\"terminate\" href=\"https://api.recurly.com/v2/subscriptions/44f83d7cba354d5b84812419f923ea96/terminate\" method=\"put\"/>\n" +
                                        "  <a name=\"postpone\" href=\"https://api.recurly.com/v2/subscriptions/44f83d7cba354d5b84812419f923ea96/postpone\" method=\"put\"/>\n" +
                                        "  <collection_method>manual</collection_method>\n" +
                                        "  <net_terms type=\"integer\">10</net_terms>\n" +
                                        "  <po_number>PO19384</po_number>\n" +
                                        "  <tax_in_cents type=\"integer\">394</tax_in_cents>\n" +
                                        "  <tax_type>usst</tax_type>\n" +
                                        "  <tax_region>CA</tax_region>\n" +
                                        "  <tax_rate type=\"float\">0.0875</tax_rate>\n" +
                                        "  <revenue_schedule_type>evenly</revenue_schedule_type>\n" +
                                        "  <first_renewal_date type=\"dateTime\">2011-07-01T07:00:00Z</first_renewal_date>\n" +
                                        "  <started_with_gift type=\"boolean\">true</started_with_gift>\n" +
                                        "  <converted_at type=\"dateTime\">2017-06-27T00:00:00Z</converted_at>" +
                                        "  <no_billing_info_reason>plan_free_trial</no_billing_info_reason>" +
                                        "  <imported_trial type=\"boolean\">true</imported_trial>" +
                                        "  <subscription_add_ons type=\"array\">\n" +
                                        "  </subscription_add_ons>\n" +
                                        "</subscription>";

        final Subscription subscription = xmlMapper.readValue(subscriptionData, Subscription.class);
        final SubscriptionRampIntervals subRamps = subscription.getRampIntervals();

        Assert.assertEquals(subRamps.size(), 3);

        Assert.assertEquals((int) subRamps.get(0).getUnitAmountInCents(), 2000);
        Assert.assertEquals((int) subRamps.get(1).getUnitAmountInCents(), 4000);
        Assert.assertEquals((int) subRamps.get(2).getUnitAmountInCents(), 7000);

        Assert.assertEquals(subRamps.get(0).getStartingOn(), new DateTime("2011-06-27T07:00:00Z"));
        Assert.assertEquals(subRamps.get(1).getStartingOn(), new DateTime("2011-07-27T07:00:00Z"));
        Assert.assertEquals(subRamps.get(2).getStartingOn(), new DateTime("2011-08-27T07:00:00Z"));

        Assert.assertEquals(subRamps.get(0).getEndingOn(), new DateTime("2011-07-27T07:00:00Z"));
        Assert.assertEquals(subRamps.get(1).getEndingOn(), new DateTime("2011-08-27T07:00:00Z"));
        Assert.assertNull(subRamps.get(2).getEndingOn());
    }

    @Test(groups = "fast")
    public void testSerializationWithSingleCoupon() throws Exception {
        Subscription subscription = TestUtils.createRandomSubscription(0);
        subscription.setCouponCode("my-coupon");
        String xmlString = xmlMapper.writeValueAsString(subscription);
        assertTrue(xmlString.contains("<coupon_codes><coupon_code>my-coupon</coupon_code></coupon_codes>"));
    }

    @Test(groups = "fast")
    public void testSerializationWithMultipleCoupon() throws Exception {
        Subscription subscription = TestUtils.createRandomSubscription(0);
        ArrayList<String> codes = new ArrayList<String>();
        codes.add("my-first-coupon");
        subscription.setCouponCodes(codes);
        String xmlString = xmlMapper.writeValueAsString(subscription);
        String xmlSubstring = "<coupon_codes><coupon_code>my-first-coupon</coupon_code></coupon_codes>";
        assertTrue(xmlString.contains(xmlSubstring));

        codes.add("my-second-coupon");
        subscription.setCouponCodes(codes);
        xmlSubstring = "<coupon_codes><coupon_code>my-first-coupon</coupon_code><coupon_code>my-second-coupon</coupon_code></coupon_codes>";
        xmlString = xmlMapper.writeValueAsString(subscription);
        assertTrue(xmlString.contains(xmlSubstring));
    }

    @Test(groups = "fast")
    public void testSerializationWithBillingInfoUuid() throws Exception {
        Subscription subscription = TestUtils.createRandomSubscription(0);
        subscription.setBillingInfoUuid("aSpecialUuid");
        String xmlString = xmlMapper.writeValueAsString(subscription);
        assertTrue(xmlString.contains("<billing_info_uuid>aSpecialUuid</billing_info_uuid>"));
    }

    @Test(groups = "fast")
    public void testHashCodeAndEquality() throws Exception {
        // create subscriptions of the same value but difference references
        Subscription subscription = TestUtils.createRandomSubscription(0);
        Subscription otherSubscription = TestUtils.createRandomSubscription(0);

        assertNotEquals(System.identityHashCode(subscription), System.identityHashCode(otherSubscription));
        assertEquals(subscription.hashCode(), otherSubscription.hashCode());
        assertEquals(subscription, otherSubscription);
    }

    private void verifySubscriptionAddons(final Subscription subscription) {
        Assert.assertEquals(subscription.getAddOns().size(), 3);
        Assert.assertEquals(subscription.getAddOns().get(0).getAddOnCode(), "extra_users");
        Assert.assertEquals(subscription.getAddOns().get(0).getQuantity(), (Integer) 2);
        Assert.assertEquals(subscription.getAddOns().get(0).getUnitAmountInCents(), (Integer) 1000);
        Assert.assertEquals(subscription.getAddOns().get(0).getUsagePercentage(), BigDecimal.valueOf(2.1));
        Assert.assertEquals(subscription.getAddOns().get(0).getRevenueScheduleType(), RevenueScheduleType.EVENLY);
        Assert.assertEquals(subscription.getAddOns().get(1).getAddOnCode(), "extra_ip");
        Assert.assertEquals(subscription.getAddOns().get(1).getQuantity(), (Integer) 3);
        Assert.assertEquals(subscription.getAddOns().get(1).getUnitAmountInCents(), (Integer) 200);
        Assert.assertEquals(subscription.getAddOns().get(2).getAddOnCode(), "mockitem");
        Assert.assertEquals(subscription.getAddOns().get(2).getAddOnSource(), "item");
        Assert.assertEquals(subscription.getAddOns().get(2).getUnitAmountInCents(), (Integer) 199);
    }

    private void verifySubscriptionCustomFields(final Subscription subscription) {
        Assert.assertEquals(subscription.getCustomFields().size(), 2);
        Assert.assertEquals(subscription.getCustomFields().get(0).getName(), "field1");
        Assert.assertEquals(subscription.getCustomFields().get(0).getValue(), "one value");
        Assert.assertEquals(subscription.getCustomFields().get(1).getName(), "field2");
        Assert.assertEquals(subscription.getCustomFields().get(1).getValue(), "second value");
    }

    private Subscription verifySubscription(final String subscriptionData) throws IOException {
        final Subscription subscription = xmlMapper.readValue(subscriptionData, Subscription.class);
        Assert.assertEquals(subscription.getUuid(), "44f83d7cba354d5b84812419f923ea96");
        Assert.assertEquals(subscription.getState(), "active");
        Assert.assertEquals(subscription.getUnitAmountInCents(), (Integer) 800);
        Assert.assertEquals(subscription.getCurrency(), "EUR");
        Assert.assertEquals(subscription.getQuantity(), (Integer) 1);
        Assert.assertEquals(subscription.getActivatedAt(), new DateTime("2011-05-27T07:00:00Z"));
        Assert.assertEquals(subscription.getUpdatedAt(), new DateTime("2011-05-27T07:00:00Z"));
        Assert.assertNull(subscription.getCanceledAt(), "");
        Assert.assertNull(subscription.getExpiresAt(), "");
        Assert.assertEquals(subscription.getCurrentPeriodStartedAt(), new DateTime("2011-06-27T07:00:00Z"));
        Assert.assertEquals(subscription.getCurrentPeriodEndsAt(), new DateTime("2010-07-27T07:00:00Z"));
        Assert.assertNull(subscription.getTrialStartedAt(), "");
        Assert.assertNull(subscription.getTrialEndsAt(), "");
        Assert.assertEquals(subscription.getStartsAt(), new DateTime("2010-07-28T07:00:00Z"));
        Assert.assertEquals(subscription.getCollectionMethod(), "manual");
        Assert.assertEquals(subscription.getNetTerms(), (Integer) 10);
        Assert.assertEquals(subscription.getPoNumber(), "PO19384");
        Assert.assertEquals(subscription.getFirstRenewalDate(), new DateTime("2011-07-01T07:00:00Z"));
        Assert.assertEquals(subscription.getRevenueScheduleType(), RevenueScheduleType.EVENLY);
        Assert.assertEquals((int) subscription.getTaxInCents(), 394);
        Assert.assertEquals(subscription.getTaxType(), "usst");
        Assert.assertEquals(subscription.getTaxRegion(), "CA");
        Assert.assertEquals(subscription.getTaxRate(), new BigDecimal("0.0875"));
        Assert.assertEquals(subscription.getConvertedAt(), new DateTime("2017-06-27T00:00:00Z"));
        Assert.assertTrue(subscription.getStartedWithGift());
        Assert.assertEquals(subscription.getNoBillingInfoReason(), "plan_free_trial");
        Assert.assertTrue(subscription.getImportedTrial());

        return subscription;
    }

    private void verifyPaginationData(final Subscription subscription) {
        // Verify nested attributes
        Assert.assertEquals(subscription.getAccount().getHref(), "https://api.recurly.com/v2/accounts/1");
        Assert.assertEquals(subscription.getAccount().getAccountCode(), "1");
    }

    private void verifyPendingSubscription(final Subscription subscription) {
        Subscription pending = subscription.getPendingSubscription();
        Assert.assertEquals(pending.getPlan().getPlanCode(), "silver");
        Assert.assertEquals(pending.getPlan().getName(), "Silver plan");
        Assert.assertEquals(pending.getUnitAmountInCents(), (Integer) 400);
        Assert.assertEquals(pending.getQuantity(), (Integer) 1);
        Assert.assertEquals(pending.getAddOns().size(), 0);
    }
}
