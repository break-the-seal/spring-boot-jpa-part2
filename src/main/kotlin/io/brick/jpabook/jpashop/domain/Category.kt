package io.brick.jpabook.jpashop.domain

import io.brick.jpabook.jpashop.domain.item.Item
import javax.persistence.*

@Entity
class Category {
    @Id
    @GeneratedValue
    @Column(name = "category_id")
    var id: Long = 0L

    var name: String = ""

    @ManyToMany
    @JoinTable(
        name = "category_item",
        joinColumns = [JoinColumn(name = "category_id")],
        inverseJoinColumns = [JoinColumn(name = "item_id")]
    )
    var items: MutableList<Item> = ArrayList()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: Category? = null

    @OneToMany(mappedBy = "parent")
    var child: MutableList<Category> = ArrayList()

    // 연관관계 메서드 //
    fun addChildCategory(child: Category) {
        this.child.add(child)
        child.parent = this
    }
}