package io.brick.jpabook.jpashop.domain

import io.brick.jpabook.jpashop.domain.item.Item
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
data class Category(
    @Id @GeneratedValue
    @Column(name = "category_id")
    val id: Long,

    val name: String,

    @ManyToMany
    @JoinTable(
        name = "category_item",
        joinColumns = [JoinColumn(name = "category_id")],
        inverseJoinColumns = [JoinColumn(name = "item_id")]
    )
    val items: MutableList<Item> = mutableListOf(),

    @ManyToOne
    @JoinColumn(name = "parent_id")
    val parent: Category? = null,

    @OneToMany(mappedBy = "parent")
    val child: MutableList<Category> = mutableListOf()
)