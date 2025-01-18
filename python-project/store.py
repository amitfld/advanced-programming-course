from collections import Counter

import yaml

import errors as err
from item import Item
from shopping_cart import ShoppingCart

class Store:
    """
    A class to represent a store with inventory and shopping cart functionality.

    Attributes:
    ----------
    _items : list
        A list of Item objects representing the store's inventory.
    _shopping_cart : ShoppingCart
        A ShoppingCart instance to manage items added by the user.

    Methods:
    -------
    get_items() -> list:
        Returns a list of all items in the store's inventory.

    get_shopping_cart_items_hashtags() -> list:
        Returns a list of hashtags from items in the shopping cart.

    is_in_shopping_cart(item: Item) -> bool:
        Checks if an item is in the shopping cart.

    count_common_hashtags(item: Item) -> int:
        Counts the number of common hashtags between the given item and items in the shopping cart.

    search_by_name(item_name: str) -> list:
        Searches for items in the inventory by name, excluding those in the shopping cart.

    search_by_hashtag(hashtag: str) -> list:
        Searches for items in the inventory by hashtag, excluding those in the shopping cart.

    add_item(item_name: str):
        Adds an item to the shopping cart by its name. Handles errors if item is not found or matches multiple items.

    search_in_shopping_cart(item_name: str) -> list:
        Searches for items in the shopping cart by name.

    remove_item(item_name: str):
        Removes an item from the shopping cart by name. Handles errors if item is not found or matches multiple items.

    checkout() -> int:
        Calculates the total price of items in the shopping cart.
    """

    def __init__(self, path):
        with open(path) as inventory:
            items_raw = yaml.load(inventory, Loader=yaml.FullLoader)['items']
        self._items = self._convert_to_item_objects(items_raw)
        self._shopping_cart = ShoppingCart()

    @staticmethod
    def _convert_to_item_objects(items_raw):
        return [Item(item['name'],
                     int(item['price']),
                     item['hashtags'],
                     item['description'])
                for item in items_raw]

    def get_items(self) -> list:
        """
        Retrieves all items from the store's inventory.

            Returns:
            -------
            list:
                A list of all Item objects in the inventory.
        """
        return self._items

    def get_shopping_cart_items_hashtags(self) -> list:
        """
        Retrieves all hashtags from items in the shopping cart.

            Returns:
            -------
            list:
                A list of hashtags from the shopping cart items.
        """
        hashtags = []
        for item in self._shopping_cart.items:
            hashtags.extend(item.hashtags)
        return hashtags

    def is_in_shopping_cart(self, item: Item) -> bool:
        """
        Checks if an item is in the shopping cart.

            Parameters:
            ----------
            item : Item
                The item to check.

            Returns:
            -------
            bool:
                True if the item is in the shopping cart, False otherwise.
        """
        return any(sc_item.name == item.name for sc_item in self._shopping_cart.items)

    def count_common_hashtags(self, item: Item) -> int:
        """
        Counts the number of common hashtags between the given item and items in the shopping cart.

            Parameters:
            ----------
            item : Item
                The item to compare against shopping cart items.

            Returns:
            -------
            int:
                The number of common hashtags.
        """
        shopping_cart_hashtags = self.get_shopping_cart_items_hashtags()
        item_hashtags = item.hashtags
        common_hashtags = 0
        for tag in shopping_cart_hashtags:
            if tag in item_hashtags:
                common_hashtags += 1
        return common_hashtags

    def search_by_name(self, item_name: str) -> list:
        """
        Searches for items in the inventory by name.

            Parameters:
            ----------
            item_name : str
                The name (or partial name) of the item to search for.

            Returns:
            -------
            list:
                A list of Item objects matching the search criteria, sorted by relevance.
        """
        search_results = []
        for item in self._items:
            if item_name in item.name and not self.is_in_shopping_cart(item):
                search_results.append(item)
        sorted_search_results = sorted(
            search_results,
            key=lambda item: (
                -self.count_common_hashtags(item),
                item.name)
        )
        return sorted_search_results

    def search_by_hashtag(self, hashtag: str) -> list:
        """
        Searches for items in the inventory by hashtag.

            Parameters:
            ----------
            hashtag : str
                The hashtag to search for.

            Returns:
            -------
            list:
                A list of Item objects matching the hashtag, sorted by relevance.
        """
        search_results = []
        for item in self._items:
            if hashtag in item.hashtags and not self.is_in_shopping_cart(item):
                search_results.append(item)
        sorted_search_results = sorted(
            search_results,
            key=lambda item: (
                -self.count_common_hashtags(item),
                item.name)
        )
        return sorted_search_results

    def add_item(self, item_name: str):
        """
        Adds an item to the shopping cart by its name.

            Parameters:
            ----------
            item_name : str
                The name of the item to add.

            Raises:
            ------
            ItemNotExistError:
                If the item is not found in the inventory or shopping cart.
            TooManyMatchesError:
                If multiple items match the given name.
        """
        items = self.search_by_name(item_name)
        items_in_cart = self.search_in_shopping_cart(item_name)
        if len(items) == 0 and len(items_in_cart) == 0:
            raise err.ItemNotExistError(f"Item '{item_name}' Doesn't exist")
        if len(items) > 1 or len(items_in_cart) > 1:
            raise err.TooManyMatchesError(f"Too many matches for '{item_name}' item")
        if len(items) != 0:
            self._shopping_cart.add_item(items[0])
        else:
            self._shopping_cart.add_item(items_in_cart[0])

    def search_in_shopping_cart(self, item_name: str) -> list:
        """
        Searches for items in the shopping cart by name.

            Parameters:
            ----------
            item_name : str
                The name of the item to search for.

            Returns:
            -------
            list:
                A list of Item objects matching the search criteria.
        """
        search_results = []
        for item in self._items:
            if item_name in item.name:
                search_results.append(item)
        return search_results

    def remove_item(self, item_name: str):
        """
        Removes an item from the shopping cart by name.

            Parameters:
            ----------
            item_name : str
                The name of the item to remove.

            Raises:
            ------
            ItemNotExistError:
                If the item is not found in the shopping cart.
            TooManyMatchesError:
                If multiple items match the given name in the shopping cart.
        """
        items = self.search_in_shopping_cart(item_name)
        if len(items) == 0:
            raise err.ItemNotExistError(f"Item '{item_name}' Doesn't exist in shopping cart")
        if len(items) > 1:
            raise err.TooManyMatchesError(f"Too many matches for '{item_name}' item in shopping cart")
        self._shopping_cart.remove_item(items[0].name)

    def checkout(self) -> int:
        """
        Calculates the total price of items in the shopping cart.

            Returns:
            -------
            int:
                The total price of items in the shopping cart.
        """
        return self._shopping_cart.get_subtotal()
