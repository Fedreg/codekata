# Kata 1

## Pricing

  I chose to pick a way for modeling price that I might want to use were
  I running a business We can compare this approach with other
  approaches provided by the team and discuss pros / cons


## Modelling an item


### Considerations


  - Cost to purchase: should include any costs associated with shipping,
    preparing for resale, etc
    
  - Desired sales price: Amount you'd love to sell the item for
  
  - Ease in replacing: Is the item easy to replace once I sell it? It is
    produced locally or do I have to wait 6 weeks for new stock?
    
  - Displaying price: Integers seem most flexible
  
  - Number on hand (current stock) : Number of items in inventory
    currently
    
  - Desired number on hand: Ideal number of items to stock at any given
    time to ensure no under/over-supply.


### Examples

  ```clj
   {:item-cost 50               ;; $0.50
    :shipping-cost-per-item 50  ;; $0.50
    :total-cost-to-purchase 100 ;; $1.00
    :desired-sales-price 200    ;; $2.00
    :ease-in-replacing  10      ;; with 0 being very hard and 10 being very easy    
    :current-number-on-hand 9
    :desired-number-on-hand 10}
  ```

### Commentary

  This is a rough approximation of data I might want to have about an
  item when I determine a rough pricing for it.  This would allow me to
  have a single source to see what kind of promotions I can place on the
  item, if I need to reduce overstock, if I should be charging more, etc

  I could then apply some manipulations such as those below on the items
  for managing the stock.  I would track the changes to the sales price
  somewhere as it changes.

### Manipulations

 ```clj
   (defn adjust-price-by-stock [item]
     (let [on-hand-stock (:current-number-on-hand item)
          [desired-stock (:desired-number-on-hand item)]
      (cond 
       (on hand > desired by 33%) (... reduce price by 33%)
        (on hand < desired by 33%) (... increase price by 33%)
        ... etc)
```
```clj
  (defn minimum-price-without-override [item]
    "Minimum price that can be charged for an item without admin approval"
    (+ item-cost shipping-cost handling-costs, etc))
```
    
   ```clj
   (defn does-buy-n-get-1-free-work-for-this-item [item number-to-buy]
    "How many does a customer need to buy to justify giving one for free"
    (case number-to-buy
      2 (...does buy 2 get 1 free pass the minimum-price check and the adjust-price-by stock check?)
      3 (...same for buy 3 get 1 free)
      ...)
```
